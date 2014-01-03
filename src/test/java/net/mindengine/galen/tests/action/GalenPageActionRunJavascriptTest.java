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
package net.mindengine.galen.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenPageActionRunJavascriptTest {
    private static final String TEST_URL = "file://" + GalenPageActionCheckTest.class.getResource("/html/page-for-js-check.html").getPath();
    
    @Test public void shouldRun_javascriptFile_andPerformActions_onBrowser() throws Exception {
        WebDriver driver = new FirefoxDriver();
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        
        WebElement element = driver.findElement(By.id("search-query"));
        assertThat("Search input should not contain any text yet", element.getAttribute("value"), is(""));
        
        GalenPageActionRunJavascript action = new GalenPageActionRunJavascript("/scripts/to-run-1.js");
        action.setJsonArguments("{prefix: 'This was'}");
        
        action.execute(browser, new GalenPageTest(), null);
        
        assertThat("Search input should contain text", element.getAttribute("value"), is("This was typed by a selenium from javascript text from imported script"));
        browser.quit();
    }
    
}
