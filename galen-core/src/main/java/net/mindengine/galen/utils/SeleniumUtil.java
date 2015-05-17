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
package net.mindengine.galen.utils;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SeleniumUtil {

    public static DesiredCapabilities getBrowserCapabilities(String driverParameter) {
        DesiredCapabilities capabilities = null;
        if (driverParameter.equalsIgnoreCase(BrowserType.FIREFOX)) {
            capabilities = DesiredCapabilities.firefox();
        }
        if (driverParameter.equalsIgnoreCase(BrowserType.IEXPLORE)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        }

        if (driverParameter.equalsIgnoreCase(BrowserType.CHROME) || driverParameter == null) {
            // chrome runs much faster
            capabilities = DesiredCapabilities.chrome();
        }
        return capabilities;
    }
}
