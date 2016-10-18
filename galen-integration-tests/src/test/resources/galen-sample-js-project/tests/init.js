this._fullProjectPath = System.getProperty("sample.project.path");
this._websitePath = System.getProperty("sample.test.website");

function copyProperties(dest, source) {
    for (var propertyName in source) {
        if (source.hasOwnProperty(propertyName)) {
            dest[propertyName] = source[propertyName];
        }
    }
}

function Device(deviceName, tags, openDriverFunction) {
    this.deviceName = deviceName;
    this.tags = tags;
    this.openDriver = openDriverFunction;
}

function inLocalBrowser(name, size, tags) {
    return new Device(name, tags, function (url) {
        globalDriver.get(url)
        resize(globalDriver, size);
        return globalDriver;
    });
}

var devices = {
    mobileEmulation: inLocalBrowser("mobile", "450x800", ["mobile"]),
    tabletEmulation: inLocalBrowser("tablet", "600x800", ["tablet"]),
    desktopFirefox: inLocalBrowser("desktop", "1100x800", ["desktop"]),
};

var TEST_USER = {
    username: "testuser@example.com",
    password: "test123"
};

this.globalDriver = null;

beforeTestSuite(function () {
    globalDriver = createDriver(null, "1024x768");
});

afterTestSuite(function (){
    globalDriver.quit();
});

function openDriverForDevice(device, url) {
    var fullPath = System.getProperty("user.dir");
    var driver = device.openDriver("file://" + _websitePath);

    session.put("driver", driver);

    return driver;
}


afterTest(function (test) {
    var driver = session.get("driver");
    if (driver != null) {
        if (test.isFailed()) {
            session.report().info("Screenshot").withAttachment("Screenshot", takeScreenshot(driver));
        }
    }
});

function _test(testNamePrefix, url, callback) {
    test(testNamePrefix + " on ${deviceName} device", function (device) {
        var driver = openDriverForDevice(device, url);
        callback.call(this, driver, device);
    });
}

function testOnAllDevices(testNamePrefix, url, callback) {
    forAll(devices, function () {
        _test(testNamePrefix, url, callback);
    });
}

function testOnDevice(device, testNamePrefix, url, callback) {
    forOnly(device, function() {
        _test(testNamePrefix, url, callback);
    });
}

function customCheckLayout(driver, specPath, tags) {
    checkLayout(driver, _fullProjectPath + "/" + specPath, tags);
}

/**
 * Used for testing layout when long words are used on major elements
 * This will only work in galen 2.2+
 */
function checkLongWordsLayout(driver, spec, tags) {
    var pageSpec = parsePageSpec({
        driver: driver, 
        spec: _fullProjectPath + "/" + spec
    });

    logged("Replace text in major elements to a single long word", function (report) {
        var longWordsObjects = pageSpec.findObjectsInGroup("longWordTest");
        for (var i = 0; i < longWordsObjects.size(); i++) {
            report.info("Changing element " + longWordsObjects.get(i));

            var locator = pageSpec.getObjects().get(longWordsObjects.get(i));
            if (locator !== null) {
                var webElement = GalenUtils.findWebElement(driver, locator);
                driver.executeScript("var element = arguments[0]; element.innerHTML=\"Freundschaftsbezeigungen\";", webElement);
            }
        }
    });

    customCheckLayout(driver, spec, tags);
}


/*
    Exporting functions to all other tests that will use this script
*/
(function (export) {
    export.devices = devices;
    export.testOnAllDevices = testOnAllDevices;
    export.TEST_USER = TEST_USER;
    export.checkLongWordsLayout = checkLongWordsLayout;
    export.customCheckLayout = customCheckLayout;
})(this);
