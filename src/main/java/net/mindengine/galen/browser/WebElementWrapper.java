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
package net.mindengine.galen.browser;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class WebElementWrapper {

    private WebElement webElement;

    public WebElementWrapper(WebElement webElement) {
        this.webElement = webElement;
    }

    public void click() {
        webElement.click();
    }

    public void submit() {
        webElement.submit();
    }

    public void sendKeys(String keys) {
        webElement.sendKeys(keys);
    }

    
    public void clear() {
        webElement.clear();
    }

    public String getTagName() {
        return webElement.getTagName();
    }

    public String getAttribute(String name) {
        return webElement.getAttribute(name);
    }

    public boolean isSelected() {
        return webElement.isSelected();
    }

    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    public String getText() {
        return webElement.getText();
    }

    public WebElementWrapper[] findElements(By by) {
        return WebDriverWrapper.convertToArray(webElement.findElements(by));
    }

    public WebElementWrapper findElement(By by) {
        return new WebElementWrapper(webElement.findElement(by));
    }

    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    public Point getLocation() {
        return webElement.getLocation();
    }

    public Dimension getSize() {
        return webElement.getSize();
    }

    public String getCssValue(String propertyName) {
        return webElement.getCssValue(propertyName);
    }

    public WebElement getOriginalWebElement() {
        return this.webElement;
    }
}
