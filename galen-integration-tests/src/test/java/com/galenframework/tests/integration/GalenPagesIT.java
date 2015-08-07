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
package com.galenframework.tests.integration;

import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionRunJavascript;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GalenPagesIT {

    private static WebDriver driver;

    public static final Map<String, String> _callbacks = new HashMap<>();

    @BeforeClass
    public void initDriver() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public void closeDriver() {
        driver.quit();
    }

    @Test
    public void listComponents_fetchingAndInteraction_onGalenPages() throws Exception {
        driver.get(getUrlToResource("/complex-page/index.html"));

        GalenPageActionRunJavascript actionRunJs = new GalenPageActionRunJavascript("/complex-page/list.test.js");
        actionRunJs.execute(new TestReport(), new SeleniumBrowser(driver), new GalenPageTest(), null);

        assertThat(_callbacks.get("list-test"), is("Amount of comments is 3"
                + "\n2nd user name is: Piet"
                + "\n2nd message is: OMG!"));
    }

    @Test
    public void insideFrameFunction_ofPageElement_shouldSwitchDriver_toThatFrame() throws Exception {
        driver.get(getUrlToResource("/frame-page/main.html"));

        GalenPageActionRunJavascript actionRunJs = new GalenPageActionRunJavascript("/frame-page/insideFrame.test.js");
        actionRunJs.execute(new TestReport(), new SeleniumBrowser(driver), new GalenPageTest(), null);

        assertThat(_callbacks.get("insideFrame-test"), is("text of link inside iframe on Main page: Some line inside frame"));
    }

    private String getUrlToResource(String resource) {
        return getClass().getResource(resource).toExternalForm();
    }
}
