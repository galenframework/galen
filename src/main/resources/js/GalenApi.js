/*******************************************************************************
 * Copyright 2014 Ivan Shubin http://galenframework.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ******************************************************************************/


function createDriver(url, size, browserType) {

    if (url == undefined) {
        url = null;
    }

    if (size == undefined) {
        size = null;
    }

    if (browserType == undefined || browserType == null) {
        browserType = "firefox";
    }

    var driver = GalenUtils.createDriver(browserType, url, size);
    return driver;
}

function createGridDriver(url, attrs) {

    var attr = function (name, defaultValue) {
        defaultValue = defaultValue || null;

        if (attrs[name] != undefined && attrs[name] != null) {
            return attrs[name];
        }
        else return defaultValue;
    };
    var browser = attr("browser");
    var browserVersion = attr("browserVersion");
    var platform = attr("platform");
    var size = attr("size");
    var dc = attr("desiredCapabilities");

    return GalenUtils.createGridDriver(url, browser, browserVersion, platform, dc, size);
}


function checkLayout(driver, pageSpecFile, includedTags, excludedTags) {

    if (includedTags == undefined) {
        includedTags = null;
    }
    if (excludedTags == undefined) {
        excludedTags = null;
    }
    if (!Array.isArray(includedTags) && includedTags != null) {
        includedTags = [includedTags];
    }
    if (!Array.isArray(excludedTags) && excludedTags != null) {
        excludedTags = [excludedTags];
    }

    GalenUtils.checkLayout(driver, pageSpecFile, includedTags, excludedTags);
}


function logged(title, callback) {
    var report = TestSession.current().getReport();
    report.sectionStart(title);
    var result = callback(report);
    report.sectionEnd();
    return result;
}

function takeScreenshot(driver) {
    return GalenUtils.takeScreenshot(driver);
}


function loadProperties(fileName) {
    return GalenUtils.loadProperties(fileName);
}

function cookie(driver, cookie) {
    logged("Setting cookie: " + cookie, function () {
        GalenUtils.cookie(driver, cookie);
    });
}

function inject(driver, script) {
    return GalenUtils.injectJavascript(driver, script);
}

function readFile(fileName) {
    return GalenUtils.readFile(fileName);
}

function resize(driver, size) {
    GalenUtils.resizeDriver(driver, size);
}


var session = {
    put: function (name, value) {
        TestSession.current().put(name, value);
    },
    get: function (name) {
        return TestSession.current().get(name);
    },
    test: function () {
        return TestSession.current().getTest();
    },
    report: function() {
        return TestSession.current().getReport();
    },
    testInfo: function () {
        return TestSession.current().getTestInfo();
    }
};

var galenConsole = {
    log: function (object) {
        if (typeof object == "string") {
            System.out.println(object);
        }
        else System.out.println(JSON.stringify(object));
    }
};

function dumpPage(driver, pageName, specPath, exportPath, maxWidth, maxHeight) {
    if (maxWidth == undefined) {
        maxWidth = null;
    }

    if (maxHeight == undefined) {
        maxHeight = null;
    }
    Galen.dumpPage(driver, pageName, specPath, exportPath, maxWidth, maxHeight);
}

(function (exports) {
    exports.createDriver = createDriver;
    exports.createGridDriver = createGridDriver;
    exports.logged = logged;
    exports.checkLayout = checkLayout;
    exports.session = session;
    exports.takeScreenshot = takeScreenshot;
    exports.loadProperties = loadProperties;
    exports.cookie = cookie;
    exports.inject = inject;
    exports.readFile = readFile;
    exports.console = galenConsole;
    exports.dumpPage = dumpPage;
    exports.resize = resize;
})(this);
