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
package net.mindengine.galen.tests.util;

import net.mindengine.galen.browser.SeleniumBrowserFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Simple helper factory to easily change webdriver for test purposes
 * 
 * @author mreinhardt (Martin Reinhardt)
 *
 */
public class WebDriverFactory {

    private static WebDriver driver;

    /**
     * 
     * @return
     */
    public static WebDriver getInstance() {
        if (driver == null) {
            // TODO use constant
            final String browser = System.getProperty("galen.default.browser");

            if (SeleniumBrowserFactory.CHROME.equals(browser)) {
                DesiredCapabilities cdc = DesiredCapabilities.chrome();
                driver = new ChromeDriver(cdc);
            }
            else if (SeleniumBrowserFactory.IE.equals(browser)) {
                driver = new InternetExplorerDriver();
            }
            else if (SeleniumBrowserFactory.PHANTOMJS.equals(browser)) {
                driver = new PhantomJSDriver();
            } else {
                // default to firefox
                driver = new FirefoxDriver();
            }
        }
        return driver;
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

}
