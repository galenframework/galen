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
package com.galenframework.page.selenium;

import java.util.List;

import com.galenframework.page.Rect;
import com.galenframework.page.PageElement;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ScreenElement extends PageElement {

    private WebDriver driver;

    public ScreenElement(WebDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rect calculateArea() {
        List<Number> size = (List<Number>)((JavascriptExecutor)driver).executeScript("return [Math.max(" +
                    "document.documentElement.scrollWidth," +
                    "document.body.offsetWidth, document.documentElement.offsetWidth," +
                    "document.body.clientWidth, document.documentElement.clientWidth)," +
                    "Math.max(" + 
                    "document.documentElement.scrollHeight," +
                    "document.body.offsetHeight, document.documentElement.offsetHeight," +
                    "document.body.clientHeight, document.documentElement.clientHeight)];"
                );
        return new Rect(0, 0, size.get(0).intValue(), size.get(1).intValue());
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
        return "";
    }

}
