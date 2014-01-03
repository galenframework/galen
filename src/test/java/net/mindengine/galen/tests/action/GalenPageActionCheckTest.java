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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.components.TestGroups;
import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

@Test(groups=TestGroups.SELENIUM)
public class GalenPageActionCheckTest {

    private static final String TEST_URL = "file://" + GalenPageActionCheckTest.class.getResource("/html/page1.html").getPath();

    

    @Test public void runsTestSuccessfully_inPredefinedBrowser() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new FirefoxDriver();
        
        GalenPageActionCheck action = new GalenPageActionCheck()
            .withIncludedTags(asList("mobile"))
            .withSpecs(asList("/html/page.spec"));
        
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        browser.changeWindowSize(new Dimension(400, 800));
        
        List<ValidationError> errors = action.execute(browser, new GalenPageTest(), validationListener);
        
        driver.quit();
        
        assertThat("Invokations should be", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 185px</msg></e>\n" +
                "</o header>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "</o menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n" +
                "</o menu-item-rss>\n"
                ));
        assertThat("Errors should not be empty", errors.size(), is(1));
    }
    
    @Test public void runsTestSuccessfully_andExlcudesSpecifiedTags() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new FirefoxDriver();
        
        GalenPageActionCheck action = new GalenPageActionCheck()
            .withIncludedTags(asList("mobile"))
            .withExcludedTags(asList("debug"))
            .withSpecs(asList("/html/page-exclusion.spec"));
    
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        browser.changeWindowSize(new Dimension(400, 800));
        
        List<ValidationError> errors = action.execute(browser, new GalenPageTest(), validationListener);
        driver.quit();
        
        assertThat("Invokations should be", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 170px</msg></e>\n" +
                "</o header>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "</o menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n" +
                "</o menu-item-rss>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(1));
    }
    
}
