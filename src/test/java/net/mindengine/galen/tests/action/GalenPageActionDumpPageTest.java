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
package net.mindengine.galen.tests.action;

import com.google.common.io.Files;
import net.mindengine.galen.api.Galen;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.components.mocks.driver.MockedDriver;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionDumpPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GalenPageActionDumpPageTest {

    @Test
    public void shouldCreate_pageDump() throws Exception {

        String pageDumpPath = Files.createTempDir().getAbsolutePath() + "/pagedump";

        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/galen4j-pagedump.json");

        GalenPageAction pageAction = new GalenPageActionDumpPage("Test page", "/specs/galen4j/pagedump.spec", pageDumpPath);

        pageAction.execute(null, new SeleniumBrowser(driver), null, null);



        assertFileExists(pageDumpPath + "/page.json");
        assertFileExists(pageDumpPath + "/page.html");

    }

    private void assertFileExists(String path) {
        assertThat("File " + path + " should exist", new File(path).exists(), is(true));
    }
}
