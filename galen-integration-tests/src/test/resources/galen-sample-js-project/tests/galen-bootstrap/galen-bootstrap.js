load("basics.js");
load("devices.js");

importClass(org.apache.commons.lang3.StringEscapeUtils);



var $galen = {
    settings: {
        website: "http://localhost",
        longWordsTesting: {
            groupName: "long_word_test",
            replaceText: "Freundschaftsbezeigungen"
        },
        imageDiffSpecGenerators: {
            "image_diff_validation": function (imagePath) {
                return "image file " + imagePath + ", map-filter denoise 1";
            },
            "image_diff_validation_blur": function (imagePath) {
                return "image file " + imagePath + ", filter blur 4, map-filter denoise 4, analyze-offset 2, error 2%";
            }
        }
    },
    devices: {},

    registerDevice: function (deviceName, device) {
        this.devices[deviceName] = device;
    },

    registerDevices: function (devices) {
        var that = this;
        forMap(devices, function (name, device) {
            that.registerDevice(name, device);
        });
    }
};


function openDriverForDevice(device, url) {
    var fullUrl = null;
    if (url !== null) {
        if (url.indexOf("http://") !== 0 && url.indexOf("https://") !== 0) {
            fullUrl = $galen.settings.website;
            if (url.indexOf("/") !== 0) {
                fullUrl += "/";
            }
            fullUrl += url;
        } else {
            fullUrl = url;
        }
    }
    else {
        fullUrl = $galen.settings.website;
    }
    if (fullUrl !== null) {
        session.report().info("Open " + fullUrl);
    }
    var driver = device.initDriver(fullUrl);
    return driver;
}


afterTest(function (test) {
    var device = session.get("device");
    if (device != null) {
        if (test.isFailed()) {
            try {
                session.report().info("Screenshot & Page source")
                    .withAttachment("screenshot.png", takeScreenshot(device.driver))
                    .withTextAttachment("page-source.txt", device.driver.getPageSource())
                    .withExtrasLink("Location", device.driver.getCurrentUrl());
            } catch (ex) {
                session.report().warn("Couldn't retrieve page information: " + ex);
            }
        }
        device.quit();
    }
});

function _test(testNamePrefix, url, callback) {
    test(testNamePrefix + " on ${deviceName} device", function (device) {
        session.put("device", device);
        var driver = openDriverForDevice(device, url);
        callback.call(this, driver, device);
    });
}

function testOnAllDevices(testNamePrefix, url, callback) {
    forAll($galen.devices, function () {
        _test(testNamePrefix, url, callback);
    });
}

function testOnDevice(device, testNamePrefix, url, callback) {
    forOnly(device, function() {
        _test(testNamePrefix, url, callback);
    });
}
function testOnDevices(devicesArray, testNamePrefix, url, callback) {
    forArray(devicesArray, function (device) {
        forOnly(device, function() {
            _test(testNamePrefix, url, callback);
        });
    });
}

function _assertMandatoryArg(args, argName) {
    if (args === undefined || args === null) {
        throw new Error("Arguments are not defined");
    } else {
        if (!args.hasOwnProperty(argName)) {
            throw new Error("Missing argument '" + argName + "'");
        }
    }
}

function _assertMandatoryArgs(args, argNames) {
    forArray(argNames, function (argName) {
        _assertMandatoryArg(args, argName);
    });
}

function parseSize(sizeText) {
    var parts = sizeText.trim().split("x");
    if (parts.length === 2) {
        return {
            width: parseInt(parts[0]),
            height: parseInt(parts[1])
        };
    } else {
        throw new Error("Incorrect size: " + sizeText);
    }
}

function randomIntValueFromRange(start, end) {
    if (start !== end) {
        return Math.floor(Math.random() * (end - start)) + start;
    } else {
        return start;
    }
}

/**
 * Generates randomized variations of screen sizes and returns an array
 */
function randomBrowserSizes(sizeRange, iterationAmount) {
    var sizes       = [],
        sizeStart   = parseSize(sizeRange[0]),
        sizeEnd     = parseSize(sizeRange[1]),
        deltaWidth  = sizeEnd.width - sizeStart.width,
        deltaHeight = sizeEnd.height - sizeStart.height,
        maxDistance = Math.max(Math.abs(deltaWidth), Math.abs(deltaHeight));

    if (iterationAmount < 1) {
        throw new Error("Amount of iterations should be greater than 0");
    }
    if (iterationAmount > maxDistance) {
        iterationAmount = maxDistance;
    }


    for (var i = 0; i < iterationAmount; i += 1) {
        var widthStart = Math.floor(sizeStart.width + deltaWidth * i / iterationAmount);
        var widthEnd = Math.floor(sizeStart.width + deltaWidth * (i + 1) / iterationAmount);

        var heightStart = Math.floor(sizeStart.height + deltaHeight * i / iterationAmount);
        var heightEnd = Math.floor(sizeStart.height + deltaHeight * (i + 1) / iterationAmount);

        var w = randomIntValueFromRange(widthStart, widthEnd);
        var h = randomIntValueFromRange(widthStart, widthEnd);
        sizes.push(w + "x" + h);
    }

    return sizes;
}

function checkMultiSizeLayout(args) {
    _assertMandatoryArgs(args, [
        "driver", "spec", "sizes"
    ]);

    //driver, spec, tags, excludedTags, widthRange, iterationAmount

    if (args.sizes.length > 0) {
        forArray(args.sizes, function (size) {
            logged("Resizing to width " + size, function () {
               resize(args.driver, size);
               checkLayout(args);
            });
        });
    } else {
        throw new Error("The sizes argument is empty");
    }
}


/**
 * Used for testing layout when long words are used on major elements
 * This will only work in galen 2.2+
 */
function checkLongWordsLayout(args) {
    _assertMandatoryArgs(args, [
        "driver", "spec"
    ]);

    var groupName = args.groupName || $galen.settings.longWordsTesting.groupName;
    var replaceText = args.replaceText || $galen.settings.longWordsTesting.replaceText;
    var escapedReplaceText = StringEscapeUtils.escapeEcmaScript(replaceText);

    var pageSpec = parsePageSpec(args);

    logged("Replace text in major elements to a single long word", function (report) {
        var longWordsObjects = pageSpec.findObjectsInGroup(groupName);
        for (var i = 0; i < longWordsObjects.size(); i++) {
            report.info("Changing element " + longWordsObjects.get(i));

            var locator = pageSpec.getObjects().get(longWordsObjects.get(i));
            if (locator !== null) {
                var webElement = GalenUtils.findWebElement(args.driver, locator);
                args.driver.executeScript("var element = arguments[0]; element.innerHTML=\"" + escapedReplaceText + "\";", webElement);
            }
        }
    });

    checkLayout(args);
}


/**
 * Will only work with galen 2.2+
 */
function checkImageDiff (args) {
    //storage, driver, spec, specGenerators
    _assertMandatoryArgs(args, [
        "driver", "spec", "storage"
    ]);

    var specGenerators = args.specGenerators || $galen.settings.imageDiffSpecGenerators;

    if (!fileExists(args.storage)) {
        makeDirectory(args.storage);
    }

    var iterationDirs = listDirectory(args.storage);

    var currentIteration = 0;

    if (iterationDirs.length > 0) {
        iterationDirs.sort( function (a, b) {
            var aInt = parseInt(a) || 0;
            var bInt = parseInt(b) || 0;
            return aInt - bInt;
        });
        var selectedFolder = iterationDirs[iterationDirs.length - 1];
        currentIteration = parseInt(selectedFolder);

        var pageSpec = parsePageSpec(args);
        pageSpec.clearSections();

        var totalObjects = 0;

        forMap(specGenerators, function (imageDiffGroupName, imageDiffGroup) {
            var imageDiffObjects = GalenUtils.listToArray(pageSpec.findObjectsInGroup(imageDiffGroupName));

            totalObjects += imageDiffObjects.length;

            forArray(imageDiffObjects, function (imageDiffObjectName) {
                pageSpec.addSpec("Image Diff Validation", 
                    imageDiffObjectName, 
                    imageDiffGroup(args.storage + "/" + selectedFolder + "/objects/" + imageDiffObjectName + ".png")
                );
            });
        });

        if (totalObjects === 0) {
            throw new Error("Couldn't find any objects for " + imageDiffGroup + " group");
        }

        logged("Verifying image diffs with #" + selectedFolder + " iteration", function () {
            checkPageSpecLayout(args.driver, pageSpec);
        });
    }
        
    currentIteration += 1;
    var iterationPath = args.storage + "/" + currentIteration;

    logged("Creating page dump to " + iterationPath, function () {
        var dumpArgs = {};
        copyProperties(dumpArgs, args);
        copyProperties(dumpArgs, {
            exportPath: iterationPath,
            onlyImages: true,
            excludedObjects: ["screen", "viewport"]
        });
        dumpPage(dumpArgs);
    });

    if (iterationDirs.length == 0) {
        throw new Error("Couldn't find any previous iterations");
    }
}


/*
    Exporting functions to all other tests that will use this script
*/
(function (export) {
    export.$galen = $galen;
    export.testOnAllDevices = testOnAllDevices;
    export.testOnDevice = testOnDevice;
    export.checkLongWordsLayout = checkLongWordsLayout;
    export.checkImageDiff = checkImageDiff;
    export.checkMultiSizeLayout = checkMultiSizeLayout;
})(this);
