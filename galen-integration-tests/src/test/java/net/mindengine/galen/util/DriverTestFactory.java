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
package net.mindengine.galen.util;

import net.mindengine.galen.config.GalenConfig;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

public class DriverTestFactory {

    /**
     * Instantiate a new WebDriver instance, respects GalenConfig
     * 
     * @see GalenConfig
     * @return a new instance of the desired WebDriver
     */
    public static WebDriver getDriver() {
        WebDriver driver = null;
        if (StringUtils.equalsIgnoreCase(GalenConfig.getConfig().getDefaultBrowser(), "chrome")) {
            driver = new ChromeDriver();
        } else {
            if (StringUtils.equalsIgnoreCase(GalenConfig.getConfig().getDefaultBrowser(), "safari")) {
                driver = new SafariDriver();
            } else {
                if (StringUtils.equalsIgnoreCase(GalenConfig.getConfig().getDefaultBrowser(), "iexplore")) {
                    driver = new InternetExplorerDriver();
                } else {
                    // default to firefox
                    driver = new FirefoxDriver();
                }
            }
        }
        return driver;
    }

}
