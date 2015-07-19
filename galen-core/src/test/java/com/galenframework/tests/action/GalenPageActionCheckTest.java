/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.action;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.IOException;

import com.galenframework.browser.Browser;
import com.galenframework.components.TestGroups;
import com.galenframework.suite.actions.GalenPageActionCheck;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.components.mocks.driver.MockedDriver;
import com.galenframework.components.validation.TestValidationListener;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

@Test(groups= TestGroups.SELENIUM)
public class GalenPageActionCheckTest {

    private static final String TEST_URL = "/GalenPageActionCheckTest/page.json";

    @Test public void runsTestSuccessfully_inPredefinedBrowser() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new MockedDriver();
        
        GalenPageActionCheck action = new GalenPageActionCheck()
            .withIncludedTags(asList("mobile"))
            .withSpec(getClass().getResource("/GalenPageActionCheckTest/page.spec").getPath());
        
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        browser.changeWindowSize(new Dimension(400, 800));
        
        action.execute(new TestReport(), browser, new GalenPageTest(), validationListener);
        
        assertThat("Invokations should be", validationListener.getInvokations(), is(
                "<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 185px</msg></e>\n" +
                "</o header>\n" +
                "<o header-text-1>\n" +
                "<SpecInside header-text-1>\n" +
                "</o header-text-1>\n" +
                "<o menu>\n" +
                "<SpecBelow menu>\n" +
                "<SpecVertically menu>\n" +
                "<SpecWidth menu>\n" +
                "<e><msg>\"menu\" width is 102% [410px] instead of 100% [400px]</msg></e>\n" +
                "</o menu>\n"
                ));
    }
    
    @Test public void runsTestSuccessfully_andExcludesSpecifiedTags() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new MockedDriver();
        
        GalenPageActionCheck action = new GalenPageActionCheck()
            .withIncludedTags(asList("mobile"))
            .withExcludedTags(asList("debug"))
            .withSpec(getClass().getResource("/GalenPageActionCheckTest/page.spec").getPath());

        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        browser.changeWindowSize(new Dimension(400, 800));
        
        action.execute(new TestReport(), browser, new GalenPageTest(), validationListener);

        assertThat("Invokations should be", validationListener.getInvokations(), is(
                "<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 185px</msg></e>\n" +
                "</o header>\n" +
                "<o header-text-1>\n" +
                "<SpecInside header-text-1>\n" +
                "</o header-text-1>\n" +
                "<o menu>\n" +
                "<SpecWidth menu>\n" +
                "<e><msg>\"menu\" width is 102% [410px] instead of 100% [400px]</msg></e>\n" +
                "</o menu>\n"
        ));
    }
    
}
