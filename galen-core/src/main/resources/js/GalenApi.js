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


/*global GalenJsApi, GalenUtils, TestSession, System, Galen, GalenCore, logged*/
/*jslint nomen: true*/

(function (exports) {
    "use strict";
    function createDriver(url, size, browserType) {

        if (url === undefined) {
            url = null;
        }

        if (size === undefined) {
            size = null;
        }

        if (browserType === undefined || browserType === null) {
            browserType = "firefox";
        }

        var driver = GalenUtils.createDriver(browserType, url, size);
        return driver;
    }

    function __galen_getAttr(attrs, name) {
        if (attrs[name] !== undefined && attrs[name] !== null) {
            return attrs[name];
        }
        return null;
    }
    function createGridDriver(url, attrs) {
        var browser = __galen_getAttr(attrs, "browser"),
            browserVersion = __galen_getAttr(attrs, "browserVersion"),
            platform = __galen_getAttr(attrs, "platform"),
            size = __galen_getAttr(attrs, "size"),
            dc = __galen_getAttr(attrs, "desiredCapabilities");

        return GalenUtils.createGridDriver(url, browser, browserVersion, platform, dc, size);
    }

    function varsToArray(vars) {
        var array = [],
            name;

        for (name in vars) {
            if (vars.hasOwnProperty(name)) {
                array.push(new GalenJsApi.JsVariable(name, vars[name]));
            }
        }

        return array;
    }

    function checkLayout(driver, pageSpecFile, includedTags, excludedTags) {
        var settings,
            screenshotFile = null,
            properties = null,
            jsVariables = [];

        if (arguments.length === 1) {
            settings = driver;

            driver = settings.driver;
            pageSpecFile = settings.spec;
            includedTags = settings.tags;
            excludedTags = settings.excludedTags;
            screenshotFile = settings.screenshot;
            properties = settings.properties;

            if (settings.vars !== undefined) {
                jsVariables = varsToArray(settings.vars);
            }
        }

        if (includedTags === undefined) {
            includedTags = null;
        }
        if (excludedTags === undefined) {
            excludedTags = null;
        }
        if (properties === undefined) {
            properties = null;
        }
        if (screenshotFile === undefined) {
            screenshotFile = null;
        }


        if (!Array.isArray(includedTags) && includedTags !== null) {
            includedTags = [includedTags];
        }
        if (!Array.isArray(excludedTags) && excludedTags !== null) {
            excludedTags = [excludedTags];
        }

        GalenJsApi.checkLayout(driver, pageSpecFile, includedTags, excludedTags, properties, screenshotFile, jsVariables);
    }

    function takeScreenshot(driver) {
        return GalenUtils.takeScreenshot(driver);
    }


    function loadProperties(fileName) {
        return GalenUtils.loadProperties(fileName);
    }

    function cookie(driver, cookieText) {
        logged("Setting cookie: " + cookieText, function () {
            GalenUtils.cookie(driver, cookieText);
        });
    }

    function inject(driver, script) {
        return GalenUtils.injectJavascript(driver, script);
    }

    function readFile(fileName) {
        return GalenUtils.readFile(fileName);
    }

    function resize(driver, size) {
        GalenJsApi.resizeDriver(driver, size);
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
        report: function () {
            return TestSession.current().getReport();
        },
        testInfo: function () {
            return TestSession.current().getTestInfo();
        }
    }, galenConsole = {
        log: function (object) {
            if (typeof object === "string") {
                System.out.println(object);
            } else {
                System.out.println(JSON.stringify(object));
            }
        }
    };

    function dumpPage(driver, pageName, specPath, exportPath, maxWidth, maxHeight, onlyImages) {
        if (maxWidth === undefined) {
            maxWidth = null;
        }

        if (maxHeight === undefined) {
            maxHeight = null;
        }

        if (onlyImages === undefined) {
            onlyImages = false;
        }

        Galen.dumpPage(driver, pageName, specPath, exportPath, maxWidth, maxHeight, onlyImages);
    }

    exports.createDriver = createDriver;
    exports.createGridDriver = createGridDriver;
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
}(this));
