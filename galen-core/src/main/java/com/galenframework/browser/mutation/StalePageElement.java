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
package com.galenframework.browser.mutation;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;

public class StalePageElement extends PageElement {
    private final Rect area;
    private final boolean present;
    private final boolean visible;
    private final String text;

    public StalePageElement(PageElement original) {
        super();
        this.area = original.getArea();
        this.present = original.isPresent();
        this.visible = original.isVisible();
        this.text = original.getText();
    }

    @Override
    protected Rect calculateArea() {
        return area;
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    @Override
    public boolean isVisible() {
        return visible;
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
        return getArea().getWidth();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return "";
    }
}
