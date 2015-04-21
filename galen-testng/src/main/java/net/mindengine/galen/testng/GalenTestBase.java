/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.testng;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.config.GalenProperty;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.utils.TestDevice;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(value = GalenListener.class)
public abstract class GalenTestBase {

    private static final Logger LOG = LoggerFactory.getLogger("Layout Tests");

    private final ThreadLocal<WebDriver> activeWebDriver = new ThreadLocal<WebDriver>();
    private final ThreadLocal<TestReport> report = new ThreadLocal<TestReport>();

    // PUBLIC API

    protected void checkLayout(TestDevice testDevice, String specPath) throws Exception {
        checkLayout(testDevice, specPath, null);
    }

    protected void checkLayout(TestDevice testDevice, String specPath, List<String> includedTags) throws Exception {
        String title = "Check layout " + specPath;
        WebDriver driver = getDriver();
        LayoutReport layoutReport = Galen.checkLayout(driver, specPath, includedTags, null, new Properties(), null);
        report.get().layout(layoutReport, title);

        if (layoutReport.errors() > 0) {
            ReportUtil.analyzeReport(driver, layoutReport, specPath, testDevice);
        }
    }

    // INTERNAL API

    @BeforeMethod
    public void initReport(Method method) {
        report.set(GalenReportsContainer.get().registerTest(method));
    }

    public synchronized WebDriver getDriver() throws Exception {
        final boolean useGrid = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_RUNINGRID).equalsIgnoreCase("true");
        final String browserType = GalenConfig.getConfig().getDefaultBrowser();
        final String grid = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_URL);
        if (activeWebDriver.get() == null) {
            WebDriver driver;
            if (useGrid) {
                LOG.debug("running locally");
                driver = SeleniumBrowserFactory.getDriver(browserType);

            } else {
                try {
                    final String gridUrl = grid + "/wd/hub";
                    LOG.info("running against a grid ", gridUrl);
                    try {
                        driver = new RemoteWebDriver(new URL(gridUrl), SeleniumBrowserFactory.getBrowserCapabilities(browserType));
                        // renew session on error
                    } catch (final WebDriverException e) {
                        driver = new RemoteWebDriver(new URL(gridUrl), SeleniumBrowserFactory.getBrowserCapabilities(browserType));
                    }
                } catch (final WebDriverException e) {
                    // TODO maybe added to config?
                    LOG.info("Skipping test execution due to WebDriver Error", e);
                    throw new SkipException("Skipping test execution due to WebDriver Error");
                }
            }
            try {
                // TODO maybe added to config?
                driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
                // TODO maybe added to config?
                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            } catch (final WebDriverException exception) {
                LOG.info("Setting timeout not working in some drive", exception);
            }
            activeWebDriver.set(driver);
        }
        return activeWebDriver.get();
    }

    /**
     * tears down the active webdriver session
     * 
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public synchronized void quitDriver() {
        if (activeWebDriver.get() != null) {
            try {
                activeWebDriver.get().quit();
                activeWebDriver.set(null);
            } catch (final WebDriverException webDriverException) {
                LOG.error("Error during quitting webdriver instance", webDriverException);
            } catch (final Exception ignored) {
                LOG.error("Error during quitting webdriver instance", ignored);
            }
        }
    }

}
