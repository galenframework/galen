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
package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecVertically;

public class SpecValidationVertically extends SpecValidationOneLine<SpecVertically> {

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
            return childArea.getLeft() + (childArea.getWidth() / 2) - (mainArea.getLeft() + (mainArea.getWidth() / 2)); 
        case LEFT:
            return childArea.getLeft() - mainArea.getLeft();
        case RIGHT:
            return childArea.getLeft() + childArea.getWidth() - (mainArea.getLeft() + mainArea.getWidth());
        }
        return 0;
    }

}
