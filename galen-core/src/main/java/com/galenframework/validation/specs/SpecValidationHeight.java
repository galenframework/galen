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
package com.galenframework.validation.specs;

import com.galenframework.page.PageElement;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecHeight;

public class SpecValidationHeight extends SpecValidationSize<SpecHeight>{

    @Override
    protected LayoutMeta createMeta(String objectName, String expectedValue, String realValue) {
        return LayoutMeta.distance(objectName, Side.TOP, objectName, Side.BOTTOM, expectedValue, realValue);
    }

    @Override
    protected String getUnitName() {
        return "height";
    }

    @Override
    protected int getSizeValue(PageElement element) {
        return element.getArea().getHeight();
    }

}
