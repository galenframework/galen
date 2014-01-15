/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.browser;

import net.mindengine.galen.config.GalenConfig;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

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
        
        String gridUrl = GalenConfig.getConfig().readMandatoryProperty("galen.browserFactory.selenium.grid.url");
        SeleniumGridBrowserFactory gridFactory = new SeleniumGridBrowserFactory(gridUrl);
        
        gridFactory.setBrowser(GalenConfig.getConfig().readProperty("galen.browserFactory.selenium.grid.browser"));
        gridFactory.setBrowserVersion(GalenConfig.getConfig().readProperty("galen.browserFactory.selenium.grid.browserVersion"));
        String platform = GalenConfig.getConfig().readProperty("galen.browserFactory.selenium.grid.platform");
        if (platform != null && !platform.trim().isEmpty()) {
            gridFactory.setPlatform(Platform.valueOf(platform.toUpperCase()));
        }
        
        return gridFactory.openBrowser();
    }

    private boolean shouldBeUsingGrid() {
        return GalenConfig.getConfig().getBooleanProperty("galen.browserFactory.selenium.runInGrid", false);
    }

    private Browser createLocalBrowser() {
        if (FIREFOX.equals(browserType)) {
            return new SeleniumBrowser(new FirefoxDriver());
        }
        else if (CHROME.equals(browserType)) {
            return new SeleniumBrowser(new ChromeDriver());
        }
        else if (IE.equals(browserType)) {
            return new SeleniumBrowser(new InternetExplorerDriver());
        }
        else throw new RuntimeException(String.format("Unknown browser type: \"%s\"", browserType));
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
