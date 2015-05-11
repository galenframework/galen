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
package net.mindengine.galen.tests.page.selenium;

import net.mindengine.galen.components.mocks.driver.MockedDriver;
import net.mindengine.galen.page.AbsentPageElement;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.page.selenium.WebPageElement;
import net.mindengine.galen.specs.page.Locator;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class SeleniumPageTest {


    private WebDriver driver;
    private Page page;

    @BeforeMethod
    public void initDriver() {
        driver = new MockedDriver("/mocks/pages/selenium-page.json");
        page = new SeleniumPage(driver);
    }

    @Test
    public void shouldProcess_simpleLocators() {
        PageElement pageElement = page.getObject(new Locator("id", "username"));
        assertThat(pageElement, instanceOf(WebPageElement.class));
        assertThat(pageElement.getText(), is("John"));
    }

    @Test
    public void shouldReturn_absentPageElement() {
        PageElement pageElement = page.getObject(new Locator("id", "blahlbha"));
        assertThat(pageElement, instanceOf(AbsentPageElement.class));
    }

    @Test
    public void shouldProcess_multiLevelLocatorWithIndex() {
        PageElement pageElement1 = page.getObject(new Locator("css", ".link")
                .withParent(new Locator("css", ".menu-item", 1)));
        assertThat(pageElement1, instanceOf(WebPageElement.class));
        assertThat(pageElement1.getText(), is("Link 1"));


        PageElement pageElement2 = page.getObject(new Locator("css", ".link")
                .withParent(new Locator("css", ".menu-item", 2)));
        assertThat(pageElement2, instanceOf(WebPageElement.class));
        assertThat(pageElement2.getText(), is("Link 2"));

        PageElement pageElement3 = page.getObject(new Locator("css", ".link")
                .withParent(new Locator("css", ".menu-item", 3)));
        assertThat(pageElement3, instanceOf(WebPageElement.class));
        assertThat(pageElement3.getText(), is("Link 3"));

        PageElement pageElement4 = page.getObject(new Locator("css", ".link")
                .withParent(new Locator("css", ".menu-item", 4)));
        assertThat(pageElement4, instanceOf(AbsentPageElement.class));

    }
}
