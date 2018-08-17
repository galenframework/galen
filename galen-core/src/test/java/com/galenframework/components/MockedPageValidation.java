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
package com.galenframework.components;

import java.util.Map;

import com.galenframework.page.PageElement;
import com.galenframework.validation.PageValidation;

public class MockedPageValidation extends PageValidation{

    public Map<String, PageElement> pageElements;
    
    public MockedPageValidation(Map<String, PageElement> pageElements) {
        super(null, null, null, null, null);
        this.pageElements = pageElements;
    }
    
    @Override
    public PageElement findPageElement(String objectName) {
        return pageElements.get(objectName);
    }

}
