/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.components.mocks.driver.MockedDriver;
import com.galenframework.components.mocks.driver.MockedDriverElement;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionRunJavascript;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class GalenPageActionRunJavascriptTest {
    private static final String TEST_URL = "/GalenPageActionRunJavascriptTest/page.json";

    @Test public void shouldRun_javascriptFile_andPerformActions_onBrowser() throws Exception {
        MockedDriver driver = new MockedDriver();
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        

        GalenPageActionRunJavascript action = new GalenPageActionRunJavascript(getClass().getResource("/scripts/to-run-1.js").getFile());
        action.setJsonArguments("{prefix: 'This was'}");
        
        action.execute(new TestReport(), browser, new GalenPageTest(), null);


        MockedDriverElement webElement = (MockedDriverElement) driver.findElement(By.id("search-query"));
        assertThat("Mocked events of element should be",
                webElement.getMockedEvents(),
                hasItems("#sendKeys: This was typed by a selenium from javascript text from imported script"));
    }
    
}
