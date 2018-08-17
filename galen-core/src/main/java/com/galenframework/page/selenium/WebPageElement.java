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
package com.galenframework.page.selenium;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.Rect;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.page.PageElement;
import com.galenframework.specs.page.Locator;

import org.openqa.selenium.*;


public class WebPageElement extends PageElement {

    private WebDriver driver;
    private WebElement webElement;
    private Locator locator;
    private String objectName;
    

    public WebPageElement(WebDriver driver, String objectName, WebElement webElement, Locator objectLocator) {
        this.driver = driver;
        this.setObjectName(objectName);
        this.setWebElement(webElement);
        this.setLocator(objectLocator);
    }

    private Rect cachedArea = null;
    
    @Override
    public Rect calculateArea() {
        if (cachedArea == null) {
            cachedArea = getAreaFinder().findArea(this);
            if (getLocator() != null && getLocator().getCorrections() != null) {
                cachedArea = correctedRect(cachedArea, getLocator().getCorrections());
            }
        }
        return cachedArea;
    }

    private AreaFinder getAreaFinder() {
        String areaFinderName = GalenConfig.getConfig().getStringProperty(GalenProperty.GALEN_BROWSER_PAGELEMENT_AREAFINDER);
        return AreaFinder.valueOf(areaFinderName.toUpperCase());
    }

    private Rect correctedRect(Rect rect, CorrectionsRect corrections) {
        return new Rect(corrections.getLeft().correct(rect.getLeft()),
                corrections.getTop().correct(rect.getTop()),
                corrections.getWidth().correct(rect.getWidth()),
                corrections.getHeight().correct(rect.getHeight()));
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    
    @Override
    public boolean isVisible() {
        try {
            return getWebElement().isDisplayed();
        }
        catch (StaleElementReferenceException e) {
            return false;
        }
    }

    @Override
    public int getWidth() {
        return getArea().getWidth();
    }

    @Override
    public int getHeight() {
        return getArea().getHeight();
    }

    @Override
    public int getLeft() {
        return getArea().getLeft();
    }

    @Override
    public int getTop() {
        return getArea().getTop();
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public String getText() {
        WebElement webElement = getWebElement();
        if ("input".equals(webElement.getTagName().toLowerCase())) {
            String value = webElement.getAttribute("value");
            if (value == null) {
                value = "";
            }
            
            return value;
        }
        else return getWebElement().getText().trim();
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return getWebElement().getCssValue(cssPropertyName);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
