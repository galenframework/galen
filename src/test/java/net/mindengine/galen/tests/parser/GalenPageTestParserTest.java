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
package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.browser.SeleniumGridBrowserFactory;
import net.mindengine.galen.parser.GalenPageTestReader;
import net.mindengine.galen.suite.GalenPageTest;

import org.openqa.selenium.Platform;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GalenPageTestParserTest {

    
    @Test(dataProvider="provideGoodSamples") public void shouldParse_galenPageTest_successfully(String text, GalenPageTest expected) {
        GalenPageTest real = GalenPageTestReader.readFrom(text);
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
        };
    }


    private Object[] test(Object...args) {
        return args;
    }
}
