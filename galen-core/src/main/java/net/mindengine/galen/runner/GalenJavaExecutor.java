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
package net.mindengine.galen.runner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.api.GalenExecutor;
import net.mindengine.galen.api.GalenReportsContainer;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.config.GalenProperty;
import net.mindengine.galen.exceptions.SetupException;
import net.mindengine.galen.exceptions.TestExecutionException;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.utils.TestDevice;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalenJavaExecutor implements GalenExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(GalenJavaExecutor.class);

    private final ThreadLocal<WebDriver> activeWebDriver = new ThreadLocal<WebDriver>();

    private void loadPage(final String uri) {
        getDriverInstance().get(uri);
    }

    public WebDriver getDriverInstance() {
        try {
            if (activeWebDriver.get() == null) {
                activeWebDriver.set(createDriver());
            }
            return activeWebDriver.get();
        } catch (Exception e) {
            throw new SetupException(e);
        }
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#createDriver()
     */
    @Override
    public WebDriver createDriver() {
        final boolean useGrid = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_RUNINGRID).equalsIgnoreCase("true");
        final String browserType = GalenConfig.getConfig().getDefaultBrowser();
        final String grid = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_URL);
        WebDriver driver;
        if (useGrid) {
            try {
                LOG.debug("running locally");
                driver = SeleniumBrowserFactory.getDriver(browserType);

                final String gridUrl = grid + "/wd/hub";
                LOG.info("running against a grid ", gridUrl);
                try {
                    driver = new RemoteWebDriver(new URL(gridUrl), SeleniumBrowserFactory.getBrowserCapabilities(browserType));
                    // renew session on error
                } catch (final WebDriverException e) {
                    driver = new RemoteWebDriver(new URL(gridUrl), SeleniumBrowserFactory.getBrowserCapabilities(browserType));
                }
            } catch (MalformedURLException e) {
                throw new SetupException(e);
            }
        } else {
            LOG.debug("running locally");
            driver = SeleniumBrowserFactory.getDriver(browserType);
        }
        try {
            // TODO maybe added to config?
            driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
            // TODO maybe added to config?
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        } catch (final WebDriverException exception) {
            LOG.info("Setting timeout not working in some drive", exception);
        }
        return driver;
    }

    @Override
    public synchronized void quitDriver() {
        if (activeWebDriver.get() != null) {
            try {
                activeWebDriver.get().quit();
                activeWebDriver.set(null);
            } catch (final WebDriverException webDriverException) {
                LOG.error("Error during quitting webdriver instance", webDriverException);
            }
        }
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice, java.lang.String,
     *      java.util.List, java.util.List)
     */
    @Override
    public void checkLayout(TestDevice testDevice, String url, List<String> specs, List<String> includedTags) {
        for (String spec : specs) {
            checkLayoutInternal(getCallerMethod(), testDevice, url, spec, includedTags, null);
        }
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice, java.lang.String,
     *      java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public void checkLayout(TestDevice testDevice, String url, String spec, List<String> includedTags, List<String> groups) {
        checkLayoutInternal(getCallerMethod(), testDevice, url, spec, includedTags, groups);
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice, java.lang.String,
     *      java.util.List)
     */
    @Override
    public void checkLayout(TestDevice testDevice, String url, final List<String> specs) {
        for (String spec : specs) {
            checkLayoutInternal(getCallerMethod(), testDevice, url, spec, null, null);
        }
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice, java.lang.String,
     *      java.util.List, java.util.List, java.util.List)
     */
    @Override
    public void checkLayout(final TestDevice testDevice, String url, List<String> specs, List<String> includedTags, List<String> groups) {
        for (String spec : specs) {
            checkLayoutInternal(getCallerMethod(), testDevice, url, spec, includedTags, groups);
        }
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice,
     *      java.lang.String,java.lang.String)
     */
    @Override
    public void checkLayout(TestDevice testDevice, String url, String spec) {
        checkLayoutInternal(getCallerMethod(), testDevice, url, spec, null, null);
    }

    /**
     * @see net.mindengine.galen.api.GalenExecutor#checkLayout(net.mindengine.galen.utils.TestDevice, java.lang.String,
     *      java.lang.String, java.util.List)
     */

    @Override
    public void checkLayout(TestDevice testDevice, String url, String spec, List<String> includedTags) {
        checkLayoutInternal(getCallerMethod(), testDevice, url, spec, includedTags, null);
    }

    /**
     * Internal Method to check the layout
     * 
     * @param testDevice
     *            used to test the url
     * @param url
     *            to check the layout
     * @param spec
     *            to be used for layout validating
     * @param includedTags
     *            to be matched
     * @param groups
     *            to be shown in the report
     * @throws Exception
     *             in case of errors or failures
     */
    protected void checkLayoutInternal(final String methodName, final TestDevice testDevice, final String url, final String spec,
            final List<String> includedTags, final List<String> groups) {
        try {
            final Date startTime = new Date();
            LOG.info("Starting layout check of specs " + spec + " at " + startTime);
            loadPage(url);
            final TestReport report = GalenReportsContainer.get().registerTest(methodName, groups);
            final LayoutReport layoutReport = Galen.checkLayout(getDriverInstance(), spec, includedTags, null, null, null);
            final Date endDate = new Date();
            LOG.info("Finshed layout check at " + endDate);
            report.layout(layoutReport, spec + " | " + testDevice.getName());
            GalenUtils.analyzeReport(getDriverInstance(), layoutReport, spec, testDevice);
        } catch (MalformedURLException e) {
            throw new SetupException(e);
        } catch (IOException e) {
            throw new TestExecutionException("Could not find all spec files", e);
        }
    }

    /**
     * Get method name of the calling class via reflection
     * 
     * @return name of the method as String
     * @throws ClassNotFoundException
     */
    private String getCallerMethod() {
        try {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();
            String callerMethodName = elements[2].getMethodName();
            return callerMethodName;
        } catch (Exception e) {
            throw new SetupException("Could not detect calling test class", e);
        }
    }

}
