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
package com.galenframework.page;

public abstract class PageElement {

    private int offsetLeft = 0;
    private int offsetTop = 0;

    private Rect cachedArea = null;


    public final Rect getArea() {
        if (cachedArea == null) {
            cachedArea = this.calculateArea().offset(offsetLeft, offsetTop);
        }
        return cachedArea;
    }

    protected abstract Rect calculateArea();

    public abstract boolean isPresent();

    public abstract boolean isVisible();
    
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLeft();
    public abstract int getTop();

    public abstract String getText();

    /**
     * Should be implemented only for WEB page element
     * @param cssPropertyName
     * @return
     */
    public abstract String getCssProperty(String cssPropertyName);

    public int getOffsetLeft() {
        return offsetLeft;
    }

    public void setOffsetLeft(int offsetLeft) {
        this.offsetLeft = offsetLeft;
    }

    public int getOffsetTop() {
        return offsetTop;
    }

    public void setOffsetTop(int offsetTop) {
        this.offsetTop = offsetTop;
    }

    public PageElement withOffset(int offsetLeft, int offsetTop) {
        setOffsetLeft(offsetLeft);
        setOffsetTop(offsetTop);
        return this;
    }
}
