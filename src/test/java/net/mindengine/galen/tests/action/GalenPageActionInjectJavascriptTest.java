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
package net.mindengine.galen.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenPageActionInjectJavascriptTest {
    private static final String TEST_URL = "file://" + GalenPageActionCheckTest.class.getResource("/html/page1.html").getPath();
    
    @Test public void shouldInject_javascript() throws IOException {
        WebDriver driver = new FirefoxDriver();
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        
        GalenPageActionInjectJavascript action = new GalenPageActionInjectJavascript("/scripts/to-inject-1.js"); 
        action.execute(browser, new GalenPageTest(), null);
        
        WebElement element = driver.findElement(By.xpath("//body/injected-tag"));
        
        assertThat("Inject tags text should be", element.getText(), is("Some injected content"));
        browser.quit();
    }
}
