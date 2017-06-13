/*******************************************************************************
 * Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.browser;

import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumGridBrowserFactory;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mreinhardt on 13.06.17.
 */
public class SeleniumGridBrowserFactoryIT {


    @Test
    public void shouldOpenBrowser() {
        SeleniumGridBrowserFactory factory = new SeleniumGridBrowserFactory("http://localhost:4444/wd/hub");
        factory.setBrowser(System.getProperty("galen.default.browser", "firefox"));
        Browser browser = factory.openBrowser();
        assertThat(browser, is(notNullValue()));
    }


    @Test
    public void shouldOpenMultipleBrowser() {
        SeleniumGridBrowserFactory factory1 = new SeleniumGridBrowserFactory("http://localhost:4444/wd/hub");
        factory1.setBrowser(System.getProperty("galen.default.browser", "firefox"));
        Browser browser1 = factory1.openBrowser();
        assertThat(browser1, is(notNullValue()));
        SeleniumGridBrowserFactory factory2 = new SeleniumGridBrowserFactory("http://localhost:4444/wd/hub");
        factory2.setBrowser("chrome");
        Browser browser2 = factory2.openBrowser();
        assertThat(browser2, is(notNullValue()));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailToOpenBrowserOnInvalidURL() {
        SeleniumGridBrowserFactory factory = new SeleniumGridBrowserFactory("http://localhost:4444");
        factory.openBrowser();
    }
}
