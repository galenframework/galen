/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.components.validation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import com.galenframework.page.AbsentPageElement;
import com.galenframework.page.AbsentPageElement;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.specs.page.Locator;

public class MockedPage implements Page {

    private HashMap<String, PageElement> elements;
    private BufferedImage screenshotImage;
    
    private HashMap<String, PageElement> locatorElements;

    public MockedPage(HashMap<String, PageElement> elements) {
        this.setElements(elements);
    }
    
    public MockedPage(HashMap<String, PageElement> elements, BufferedImage screenshotImage) {
        this.setElements(elements);
        this.screenshotImage = screenshotImage;
    }
    
    public MockedPage() {
    }

    @Override
    public PageElement getObject(Locator objectLocator) {
        if (locatorElements != null) {
            PageElement pageElement = locatorElements.get(objectLocator.prettyString());
            if (pageElement != null) {
                return pageElement;
            }
        }
        return new AbsentPageElement();
    }

    @Override
    public PageElement getObject(String objectName, Locator locator) {
        if (elements != null) {
            return elements.get(objectName);
        } else {
            return null;
        }
    }

    public HashMap<String, PageElement> getElements() {
        return elements;
    }

    public void setElements(HashMap<String, PageElement> elements) {
        this.elements = elements;
    }

    @Override
    public PageElement getSpecialObject(String objectName) {
        return null;
    }

    @Override
    public int getObjectCount(Locator locator) {
        return 0;
    }

    @Override
    public Page createObjectContextPage(Locator mainObjectLocator) {
        return null;
    }


    @Override
    public void setScreenshot(File screenshotFile) {
    }

    @Override
    public BufferedImage getScreenshotImage() {
        return screenshotImage;
    }

    @Override
    public File getScreenshotFile() {
        return null;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void switchToFrame(PageElement mainObject) {

    }

    @Override
    public void switchToParentFrame() {

    }

    @Override
    public Page createFrameContext(PageElement mainObject) {
        return null;
    }

    public void setScreenshotImage(BufferedImage screenshotImage) {
        this.screenshotImage = screenshotImage;
    }

    public HashMap<String, PageElement> getLocatorElements() {
        return locatorElements;
    }

    public void setLocatorElements(HashMap<String, PageElement> locatorElements) {
        this.locatorElements = locatorElements;
    }

}