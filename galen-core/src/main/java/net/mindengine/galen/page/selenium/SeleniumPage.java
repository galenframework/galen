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
package net.mindengine.galen.page.selenium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.page.AbsentPageElement;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.rainbow4j.Rainbow4J;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumPage implements Page {

    private WebDriver driver;
    
    private Map<String, List<PageElement>> cachedElementsList = new HashMap<String, List<PageElement>>();
    private Map<String, PageElement> cachedPageElements = new HashMap<String, PageElement>();
    
    private WebElement objectContext;
    private PageElement parentObject;

    private BufferedImage cachedScreenshotImage;
    private File cachedScreenshotFile;
    private int offsetLeft = 0;
    private int offsetTop = 0;


    public SeleniumPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public SeleniumPage(WebDriver driver, Locator objectContextLocator) {
        this.driver = driver;
        setObjectContext(objectContextLocator);
    }

    
    private void setObjectContext(Locator objectContextLocator) {
        if (objectContextLocator != null) {
            By by = by(objectContextLocator);
            if (by == null) {
                throw new RuntimeException("Cannot convert locator: " + objectContextLocator.getLocatorType() + " " + objectContextLocator.getLocatorValue());
            }
            
            int index = objectContextLocator.getIndex();
            if (index > 1) {
                index = index - 1;
                List<WebElement> elements = driver.findElements(by);
                if (index >= elements.size()) {
                    throw new RuntimeException("Incorrect locator for object context. Index out of range");
                }
                objectContext = elements.get(index);
            }
            else {
                objectContext = driver.findElement(by);
            }


            this.parentObject = new WebPageElement("parent", objectContext, objectContextLocator)
                    .withOffset(offsetLeft, offsetTop);
        }
    }


    @Override
    public PageElement getObject(Locator objectLocator) {
        return getObject("unnamed", objectLocator);
    }

    @Override
    public PageElement getObject(String objectName, Locator objectLocator) {
        int index = objectLocator.getIndex() - 1;
        
        if (index >= 0) {
            return getWebPageElement(objectName, objectLocator, index);
        }
        else {
            return getWebPageElement(objectName, objectLocator);
        }
    }

    private PageElement getWebPageElement(String objectName, Locator objectLocator, int index) {
        List<PageElement> pageElements = cachedElementsList.get(objectName);
        
        if (pageElements == null) {
            By by = by(objectLocator);
            if (by == null) {
                return null;
            }
            
            List<WebElement> webElements = driverFindElements(by);
            
            pageElements = new LinkedList<PageElement>();
            int i = 1;
            for (WebElement webElement : webElements) {
                pageElements.add(new WebPageElement(objectName, webElement, new Locator(objectLocator.getLocatorType(), objectLocator.getLocatorValue(), i))
                    .withOffset(offsetLeft, offsetTop));
                i++;
            }
        }
        if (index < pageElements.size()) {
             return pageElements.get(index);
        }
        else {
            return new AbsentPageElement();
        }
        
    }

    private List<WebElement> driverFindElements(By by) {
        if (objectContext == null) {
            return driver.findElements(by);
        }
        else {
            return objectContext.findElements(by);
        }
    }

    private WebElement driverFindElement(By by) {
        if (objectContext == null) {
            return driver.findElement(by);
        }
        else {
            return objectContext.findElement(by);
        }
    }
    

    private PageElement getWebPageElement(String objectName, Locator objectLocator) {
        PageElement pageElement = cachedPageElements.get(objectName);
        
        if (pageElement == null) {
            pageElement = locatorToElement(objectName, objectLocator);
            
            cachedPageElements.put(objectName, pageElement);
            return pageElement;
        }
        else {
            return pageElement;
        }
    }

    private PageElement locatorToElement(String objectName, Locator objectLocator) {
        PageElement pageElement;
        By by = by(objectLocator);
        if (by == null) {
            return null;
        }
        
        try {
            WebElement webElement = driverFindElement(by);
            pageElement = new WebPageElement(objectName, webElement, objectLocator).withOffset(offsetLeft, offsetTop);
        }
        catch (NoSuchElementException e) {
            pageElement = new AbsentPageElement();
        }
        return pageElement;
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
            return new ScreenElement(driver).withOffset(offsetLeft, offsetTop);
        }
        else if ("viewport".equals(objectName)) {
            return new ViewportElement(driver);
        }
        else if ("parent".equals(objectName)) {
            if (parentObject != null) {
                return parentObject;
            }
            else throw new RuntimeException("There is no parent object defined on page");
        }
        else return null;
    }


    @Override
    public int getObjectCount(Locator locator) {
        return driverFindElements(by(locator)).size();
    }

    @Override
    public Page createObjectContextPage(Locator objectContextLocator) {
        return new SeleniumPage(this.driver, objectContextLocator);
    }

    @Override
    public File createScreenshot() {
        if (this.cachedScreenshotFile == null) {
            cachedScreenshotFile = new SeleniumBrowser(driver).createScreenshot();
        }

        return this.cachedScreenshotFile;
    }

    @Override
    public void setScreenshot(File screenshotFile) {
        this.cachedScreenshotFile = screenshotFile;
    }

    @Override
    public BufferedImage getScreenshotImage() {
        if (this.cachedScreenshotImage == null) {
            try {
                cachedScreenshotImage = Rainbow4J.loadImage(createScreenshot().getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException("Couldn't take screenshot for page", e);
            }
        }
        return this.cachedScreenshotImage;
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public void switchToFrame(PageElement mainObject) {
        WebPageElement webPageElement = (WebPageElement)mainObject;
        driver.switchTo().frame(webPageElement.getWebElement());
    }

    @Override
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }

    @Override
    public Page createFrameContext(PageElement frameElement) {
        SeleniumPage framePage = new SeleniumPage(driver);

        Rect mainObjectArea = frameElement.getArea();
        framePage.setOffset(mainObjectArea.getLeft(), mainObjectArea.getTop());
        framePage.switchToFrame(frameElement);
        framePage.setParentObject(frameElement);
        return framePage;
    }

    private void setOffset(int offsetLeft, int offsetTop) {
        this.offsetLeft = offsetLeft;
        this.offsetTop = offsetTop;
    }

    public PageElement getParentObject() {
        return parentObject;
    }

    public void setParentObject(PageElement parentObject) {
        this.parentObject = parentObject;
    }

}
