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

function createDriver(url, size) {

    if (url == undefined) {
        url = null;
    }

    if (size == undefined) {
        size = null;
    }

    var driver = GalenUtils.createDriver("firefox", url, size);
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
    var dc = attr("desiredCapabilities");

    return GalenUtils.createGridDriver(url, browser, browserVersion, platform, dc);
}


function checkLayout(driver, pageSpecFile, includedTags, excludedTags) {
    GalenUtils.checkLayout(driver, pageSpecFile, includedTags, excludedTags);
}


function logged(title, callback) {
    var report = TestSession.current().getReport();
    report.sectionStart(title);
    var result = callback(report);
    report.sectionEnd();
    return result;
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


(function (exports) {
    exports.createDriver = createDriver;
    exports.createGridDriver = createGridDriver;
    exports.logged = logged;
    exports.checkLayout = checkLayout;
    exports.session = session;
})(this);
