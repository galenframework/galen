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
package com.galenframework.browser.mutation;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.suite.actions.mutation.AreaMutation;

public class MutatedPageElement extends PageElement {
    private final PageElement element;
    private final AreaMutation mutation;

    public MutatedPageElement(PageElement element, AreaMutation mutation) {
        this.element = element;
        this.mutation = mutation;
    }

    @Override
    protected Rect calculateArea() {
        return mutation.mutate(element.getArea());
    }

    @Override
    public boolean isPresent() {
        return element.isPresent();
    }

    @Override
    public boolean isVisible() {
        return element.isVisible();
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
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
        return element.getText();
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return element.getCssProperty(cssPropertyName);
    }
}
