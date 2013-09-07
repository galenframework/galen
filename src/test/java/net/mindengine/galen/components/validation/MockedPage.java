/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.components.validation;

import java.util.HashMap;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.page.Locator;

public class MockedPage implements Page {

    private HashMap<String, PageElement> elements;

    public MockedPage(HashMap<String, PageElement> elements) {
        this.setElements(elements);
    }

    @Override
    public PageElement getObject(String objectName, Locator locator) {
        return getElements().get(objectName);
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

}