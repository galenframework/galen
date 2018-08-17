/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
******************************************************************************/
package com.galenframework.config;

public enum GalenProperty {
    SCREENSHOT_AUTORESIZE("galen.screenshot.autoresize", "true"),
    SCREENSHOT_FULLPAGE("galen.browser.screenshots.fullPage", "false"),

    // smart waiting for scroll position, but with a timeout, set to zero to turn off smart wait
    SCREENSHOT_FULLPAGE_SCROLLTIMEOUT("galen.browser.screenshots.fullPage.scrollTimeout", "250"),

    // hard wait during scroll
    SCREENSHOT_FULLPAGE_SCROLLWAIT("galen.browser.screenshots.fullPage.scrollWait", "0"),
    SPEC_IMAGE_TOLERANCE("galen.spec.image.tolerance", "25"),
    SPEC_IMAGE_ERROR_RATE("galen.spec.image.error", "0px"),
    SPEC_GLOBAL_VISIBILITY_CHECK("galen.spec.global.visibility", "true"),

    TEST_JS_SUFFIX("galen.test.js.file.suffix", ".test.js"),
    TEST_SUFFIX("galen.test.file.suffix", ".test"),
    
    TEST_JAVA_REPORT_OUTPUTFOLDER("galen.test.java.report.outputFolder", "target/galen-html-reports"),
    TEST_JAVA_TIMEOUT_PAGELOAD("galen.test.java.timeout.pageLoad", "20"),
    TEST_JAVA_TIMEOUT_IMPLICITYWAIT("galen.test.java.timeout.implicitWait", "1"),
    
    GALEN_CONFIG_FILE("galen.config.file", "galen.config"),
    GALEN_RANGE_APPROXIMATION("galen.range.approximation", "2"),
    GALEN_REPORTING_LISTENERS("galen.reporting.listeners", ""),
    GALEN_DEFAULT_BROWSER("galen.default.browser", "firefox"),
    GALEN_LOG_LEVEL("galen.log.level", "10"),
    GALEN_USE_FAIL_EXIT_CODE("galen.use.fail.exit.code", "true"),

    SPEC_COLORSCHEME_TOLERANCE("spec.colorscheme.tolerance", "3"),

    GALEN_BROWSER_HEADLESS("galen.browser.headless", "false"),
    GALEN_BROWSERFACTORY_SELENIUM_RUNINGRID("galen.browserFactory.selenium.runInGrid", "false"),
    GALEN_BROWSERFACTORY_SELENIUM_GRID_URL("galen.browserFactory.selenium.grid.url", null),
    GALEN_BROWSERFACTORY_SELENIUM_GRID_BROWSER("galen.browserFactory.selenium.grid.browser", null),
    GALEN_BROWSERFACTORY_SELENIUM_GRID_BROWSERVERSION("galen.browserFactory.selenium.grid.browserVersion", null),
    GALEN_BROWSERFACTORY_SELENIUM_GRID_PLATFORM("galen.browserFactory.selenium.grid.platform", null),

    GALEN_BROWSER_VIEWPORT_ADJUSTSIZE("galen.browser.viewport.adjustSize", "false"),

    GALEN_BROWSER_PAGELEMENT_AREAFINDER("galen.browser.pageElement.areaFinder", "native"),
    GALEN_BROWSER_PAGELEMENT_AREAFINDER_CUSTOM_SCRIPT("galen.browser.pageElement.areaFinder.custom.script", null),

    FILE_CREATE_TIMEOUT("galen.file.wait.timeout", "30");


    protected final String propertyName;
    protected final String defaultValue;

    GalenProperty(String propertyName, String defaultValue) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
    }
}
