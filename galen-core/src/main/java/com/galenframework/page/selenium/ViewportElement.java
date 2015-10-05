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
package com.galenframework.page.selenium;

import java.awt.*;

import com.galenframework.page.Rect;
import com.galenframework.utils.GalenUtils;
import com.galenframework.page.PageElement;

import org.openqa.selenium.WebDriver;

public class ViewportElement extends PageElement {

    private WebDriver driver;

    public ViewportElement(WebDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rect calculateArea() {
        Dimension viewportArea = GalenUtils.getViewportArea(driver);
        return new Rect(0, 0, viewportArea.width, viewportArea.height);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return true;
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
        return 0;
    }

    @Override
    public int getTop() {
        return 0;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return null;
    }

}
