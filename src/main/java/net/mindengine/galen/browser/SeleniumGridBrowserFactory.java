/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

import java.net.URL;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumGridBrowserFactory implements BrowserFactory {

    private String gridUrl;
    private String browser;
    private String browserVersion;
    private Platform platform;

    public SeleniumGridBrowserFactory(String gridUrl) {
        this.setGridUrl(gridUrl);
    }

    @Override
    public Browser openBrowser() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        
        if (platform != null) {
            desiredCapabilities.setPlatform(platform);
        }
        
        if (browser != null) {
            desiredCapabilities.setBrowserName(browser);
        }
        
        if (browserVersion != null) {
            desiredCapabilities.setVersion(browserVersion);
        }
        try {
        	return new SeleniumBrowser(new RemoteWebDriver(new URL(gridUrl), desiredCapabilities));
        }
        catch (Exception ex) {
        	throw new RuntimeException(ex);
        }
    }

    public SeleniumGridBrowserFactory withBrowser(String browser) {
        this.setBrowser(browser);
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public SeleniumGridBrowserFactory withBrowserVersion(String browserVersion) {
        this.setBrowserVersion(browserVersion);
        return this;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public SeleniumGridBrowserFactory withPlatform(Platform platform) {
        this.setPlatform(platform);
        return this;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getGridUrl() {
        return gridUrl;
    }

    public void setGridUrl(String gridUrl) {
        this.gridUrl = gridUrl;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.browser)
            .append(this.browserVersion)
            .append(this.gridUrl)
            .append(this.platform)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("browser", this.browser)
            .append("browserVersion", this.browserVersion)
            .append("gridUrl", this.gridUrl)
            .append("platform", this.platform)
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
        if (!(obj instanceof SeleniumGridBrowserFactory)) {
            return false;
        }
        SeleniumGridBrowserFactory rhs = (SeleniumGridBrowserFactory)obj;
        
        return new EqualsBuilder()
            .append(this.browser, rhs.browser)
            .append(this.browserVersion, rhs.browserVersion)
            .append(this.gridUrl, rhs.gridUrl)
            .append(this.platform, rhs.platform)
            .isEquals();
    }

}
