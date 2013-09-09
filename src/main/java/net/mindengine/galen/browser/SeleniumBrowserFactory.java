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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumBrowserFactory implements BrowserFactory {

    public static final String FIREFOX = "firefox";
    public static final String CHROME = "chrome";
    public static final String IE = "ie";
    private String browserType = FIREFOX;

    public SeleniumBrowserFactory(String browserType) {
        this.browserType = browserType;
    }

    public SeleniumBrowserFactory() {
    }

    @Override
    public Browser openBrowser() {
        if (FIREFOX.equals(browserType)) {
            return new SeleniumBrowser(new FirefoxDriver());
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
