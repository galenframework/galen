/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.components.mocks.driver;

import org.openqa.selenium.*;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class MockedDriverElement implements WebElement, TakesScreenshot {
    private final MockedPageItem item;

    public MockedDriverElement(MockedPageItem item) {
        this.item = item;
    }

    @Override
    public void click() {

    }

    @Override
    public void submit() {

    }

    @Override
    public void sendKeys(CharSequence... charSequences) {

        StringBuilder builder = new StringBuilder();
        for (CharSequence charSequence : charSequences) {
            builder.append(charSequence.toString());
        }

        registerEvent("#sendKeys: " + builder.toString());
    }

    @Override
    public void clear() {

    }

    @Override
    public String getTagName() {
        return "div";
    }

    @Override
    public String getAttribute(String s) {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getText() {
        if (item.getText() != null) {
            return item.getText();
        }
        else return "";
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> webElements = new LinkedList<>();
        if (item.getSubItems() != null) {
            for (MockedPageItem subItem : item.getSubItems()) {
                if (subItem.matches(by)) {
                    webElements.add(subItem.asWebElement());
                }
            }
        }

        return webElements;
    }

    @Override
    public WebElement findElement(By by) {
        if (item.getSubItems() != null) {
            for (MockedPageItem subItem : item.getSubItems()) {
                if (subItem.matches(by)) {
                    return subItem.asWebElement();
                }
            }
        }

        throw new NoSuchElementException(by.toString());
    }

    @Override
    public boolean isDisplayed() {
        return item.isVisible();
    }

    @Override
    public Point getLocation() {
        if (item.getArea() == null) {
            throw new RuntimeException("Element doesn't have area");
        }
        return new Point(item.getArea()[0], item.getArea()[1]);
    }


    @Override
    public Dimension getSize() {
        if (item.getArea() == null) {
            throw new RuntimeException("Element doesn't have area");
        }
        return new Dimension(item.getArea()[2], item.getArea()[3]);
    }

    @Override
    public String getCssValue(String s) {
        return null;
    }

    public List<String> getMockedEvents() {
        return this.item.getMockedEvents();
    }

    private void registerEvent(String event) {
        this.item.getMockedEvents().add(event);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return null;
    }
}
