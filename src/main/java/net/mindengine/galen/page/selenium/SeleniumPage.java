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
package net.mindengine.galen.page.selenium;

import net.mindengine.galen.page.AbsentPageElement;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.page.Locator;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class SeleniumPage implements Page {

    private WebDriver driver;

    public SeleniumPage(WebDriver driver) {
        this.driver = driver;
    }

    
    @Override
    public PageElement getObject(String objectName, Locator objectLocator) {
        By by = by(objectLocator);
        if (by != null) {
            try {
                return new WebPageElement(objectName, driver.findElement(by), objectLocator);
            }
            catch (NoSuchElementException e) {
                return new AbsentPageElement();
            }
        }
        return null;
    }

    private By by(Locator locator) {
        if ("xpath".equals(locator.getLocatorType())) {
            return By.xpath(locator.getLocatorValue());
        }
        else if ("id".equals(locator.getLocatorType())) {
            return By.id(locator.getLocatorValue());
        }
        else if ("css".equals(locator.getLocatorType())) {
            return By.cssSelector(locator.getLocatorValue());
        }
        else return null;
    }


    @Override
    public PageElement getSpecialObject(String objectName) {
        if ("screen".equals(objectName)) {
            return new ScreenElement(driver);
        }
        else return null;
    }

}
