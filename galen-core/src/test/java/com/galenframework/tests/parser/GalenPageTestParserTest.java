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
package com.galenframework.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import com.galenframework.browser.JsBrowserFactory;
import com.galenframework.browser.SeleniumGridBrowserFactory;
import com.galenframework.parser.GalenPageTestReader;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.browser.JsBrowserFactory;
import com.galenframework.browser.SeleniumBrowserFactory;
import com.galenframework.browser.SeleniumGridBrowserFactory;
import com.galenframework.config.GalenConfig;
import com.galenframework.parser.GalenPageTestReader;
import com.galenframework.suite.GalenPageTest;

import org.openqa.selenium.Platform;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GalenPageTestParserTest {
    
    private      String browser ;
    
    @BeforeClass
    public void setup() throws IOException {
        // ignore test parameter
        browser = System.getProperty("galen.default.browser");
        System.getProperties().remove("galen.default.browser");
        GalenConfig.getConfig().reset();
    }

    
    @AfterClass
    public void tearDown() {
    	if(browser!=null){
    		System.setProperty("galen.default.browser", browser);
        }    	
    }
    
    @Test(dataProvider="provideGoodSamples") public void shouldParse_galenPageTest_successfully(String text, GalenPageTest expected) {
        GalenPageTest real = GalenPageTestReader.readFrom(text, null);
        assertThat(real, is(expected));
    }
    
    
    @DataProvider public Object[][] provideGoodSamples() {
        return new Object[][]{
           test("http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory())),
           
           test("selenium firefox http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory())),
           
           test("selenium chrome http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.CHROME))),
           
           test("selenium ie http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.IE))),

           test("selenium phantomjs http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory("phantomjs"))),

           test("selenium whatever_other_browser http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory("whatever_other_browser"))),
                   
           test("Selenium Chrome http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.CHROME))),
                   
           test("SELENIUM CHROME http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.CHROME))),
                   
           test("selenium grid http://mygrid:8080/wd/hub --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub"))),
                   
           test("selenium grid http://mygrid:8080/wd/hub --browser chrome --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                           .withBrowser("chrome"))),
                   
           test("selenium grid http://mygrid:8080/wd/hub --browser chrome --version 21.1 --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                           .withBrowser("chrome")
                           .withBrowserVersion("21.1"))),
                   
           test("selenium grid http://mygrid:8080/wd/hub --browser chrome --version 21.1 --platform XP --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                           .withBrowser("chrome")
                           .withBrowserVersion("21.1")
                           .withPlatform(Platform.XP))),
                   
           test("selenium grid http://mygrid:8080/wd/hub --browser chrome --version 21.1 --platform WIN8 --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                           .withBrowser("chrome")
                           .withBrowserVersion("21.1")
                           .withPlatform(Platform.WIN8))),
                           
           test("selenium grid http://mygrid:8080/wd/hub --dc.device-orientation portrait --dc.platform \"OS X 10.0\" --page http://example.org --size 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)
                   .withBrowserFactory(new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                       .withDesiredCapability("device-orientation", "portrait")
                       .withDesiredCapability("platform", "OS X 10.0"))),
                       
                       
           test("jsfactory script.js http://example.com 640x480", new GalenPageTest()
                   .withBrowserFactory(new JsBrowserFactory("script.js", new String[]{"http://example.com", "640x480"})))
        };
    }


    private Object[] test(Object...args) {
        return args;
    }
}
