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
package com.galenframework.parser;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;

/**
 * Created by ishubin on 2014/11/02.
 */
public class JsPageElement {
    private PageElement pageElement;

    public String name;

    public JsPageElement(String name, PageElement pageElement) {
        this.name = name;
        this.pageElement = pageElement;
    }

    public PageElement getPageElement() {
        return pageElement;
    }

    public int left() {
        return pageElement.getArea().getLeft();
    }
    public int top() {
        return pageElement.getArea().getTop();
    }
    public int right() {
        Rect area = pageElement.getArea();
        return area.getLeft() + area.getWidth();
    }
    public int bottom() {
        Rect area = pageElement.getArea();
        return area.getTop() + area.getHeight();
    }

    public int width() {
        return pageElement.getArea().getWidth();
    }

    public int height() {
        return pageElement.getArea().getHeight();
    }

    public boolean isVisible() {
        return pageElement.isVisible();
    }

    public boolean isPresent() {
        return pageElement.isPresent();
    }
}
