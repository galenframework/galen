/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.browser;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

/**
 * This is a general browser factory which could also 
 * be configured to run in Selenium Grid via config file.
 * @author Ivan Shubin
 *
 */
public class SeleniumBrowserFactory implements BrowserFactory {

    public static final String FIREFOX = "firefox";
    public static final String CHROME = "chrome";
    public static final String IE = "ie";
    public static final String PHANTOMJS = "phantomjs";
    public static final String SAFARI = "safari";
    public static final String EDGE = "edge";
    private String browserType = GalenConfig.getConfig().getDefaultBrowser();

    public SeleniumBrowserFactory(String browserType) {
        this.browserType = browserType;
    }

    public SeleniumBrowserFactory() {
    }

    @Override
    public Browser openBrowser() {
        
        if (shouldBeUsingGrid()) {
            return createSeleniumGridBrowser();
        }
        else {
            return createLocalBrowser();
        }
    }

    private Browser createSeleniumGridBrowser() {
        
        String gridUrl = GalenConfig.getConfig().readMandatoryProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_URL);
        SeleniumGridBrowserFactory gridFactory = new SeleniumGridBrowserFactory(gridUrl);
        
        gridFactory.setBrowser(GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_BROWSER));
        gridFactory.setBrowserVersion(GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_BROWSERVERSION));
        String platform = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_GRID_PLATFORM);
        if (platform != null && !platform.trim().isEmpty()) {
            gridFactory.setPlatform(Platform.valueOf(platform.toUpperCase()));
        }
        
        return gridFactory.openBrowser();
    }

    private boolean shouldBeUsingGrid() {
        return GalenConfig.getConfig().getBooleanProperty(GalenProperty.GALEN_BROWSERFACTORY_SELENIUM_RUNINGRID);
    }

    private Browser createLocalBrowser() {
        return new SeleniumBrowser(SeleniumBrowserFactory.getDriver(browserType));
    }
    
    public static WebDriver getDriver(String browserType){
        
        if ( StringUtils.isEmpty(browserType) || FIREFOX.equals(browserType)) {
            return new FirefoxDriver(SeleniumBrowserFactory.getBrowserCapabilities(browserType));
        }
        else if (CHROME.equals(browserType)) {
            return new ChromeDriver(SeleniumBrowserFactory.getBrowserCapabilities(browserType));
        }
        else if (IE.equals(browserType)) {
            return new InternetExplorerDriver(SeleniumBrowserFactory.getBrowserCapabilities(browserType));
        }
        else if (PHANTOMJS.equals(browserType)) {
            return new PhantomJSDriver();
        }
        else if (SAFARI.equals(browserType)) {
            return new SafariDriver();
        }
        else if (EDGE.equals(browserType)) {
            return new EdgeDriver();
        }
        else {
            throw new RuntimeException(String.format("Unknown browser type: \"%s\"", browserType));
        }
    }

    public static DesiredCapabilities getBrowserCapabilities(String driverParameter) {
        DesiredCapabilities capabilities = null;
        if (driverParameter.equalsIgnoreCase(FIREFOX)) {
            capabilities = DesiredCapabilities.firefox();
        }
        if (driverParameter.equalsIgnoreCase(IE)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        }
        if (driverParameter.equalsIgnoreCase(CHROME)) {
            capabilities = DesiredCapabilities.chrome();
        }
        return capabilities;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(browserType)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("browserType", this.browserType)
            .toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SeleniumBrowserFactory)) {
            return false;
        }
        
        SeleniumBrowserFactory rhs = (SeleniumBrowserFactory)obj;
        
        return new EqualsBuilder()
            .append(this.browserType, rhs.browserType)
            .isEquals();
    }
}
