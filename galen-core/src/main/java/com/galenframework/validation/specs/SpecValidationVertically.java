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

import com.galenframework.page.Rect;
import com.galenframework.specs.SpecVertically;
import com.galenframework.page.PageElement;

public class SpecValidationVertically extends SpecValidationAligned<SpecVertically> {

    @Override
    protected String getAligmentText(SpecVertically spec) {
        return String.format("vertically %s", spec.getAlignment().toString());
    }

    @Override
    protected int getOffset(SpecVertically spec, PageElement mainObject, PageElement childObject) {
        Rect mainArea = mainObject.getArea();
        Rect childArea = childObject.getArea();
        
        switch(spec.getAlignment()) {
        case CENTERED:
            return Math.abs(childArea.getLeft() + (childArea.getWidth() / 2) - (mainArea.getLeft() + (mainArea.getWidth() / 2))); 
        case LEFT:
            return Math.abs(childArea.getLeft() - mainArea.getLeft());
        case RIGHT:
            return Math.abs(childArea.getLeft() + childArea.getWidth() - (mainArea.getLeft() + mainArea.getWidth()));
        case ALL:
            return Math.max(Math.abs(childArea.getLeft() - mainArea.getLeft()), Math.abs(childArea.getWidth() - mainArea.getWidth()));
        }
        return 0;
    }

}
