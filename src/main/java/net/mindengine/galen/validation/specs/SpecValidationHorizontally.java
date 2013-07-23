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
import net.mindengine.galen.specs.SpecHorizontally;

public class SpecValidationHorizontally extends SpecValidationOneLine<SpecHorizontally> {

    @Override
    protected String getAligmentText(SpecHorizontally spec) {
        return String.format("horizontally %s", spec.getAlignment().toString());
    }

    @Override
    protected int getOffset(SpecHorizontally spec, PageElement mainObject, PageElement childObject) {
        Rect mainArea = mainObject.getArea();
        Rect childArea = childObject.getArea();
        
        switch(spec.getAlignment()) {
        case CENTERED:
            return childArea.getTop() + (childArea.getHeight() / 2) - (mainArea.getTop() + (mainArea.getHeight() / 2)); 
        case TOP:
            return childArea.getTop() - mainArea.getTop();
        case BOTTOM:
            return childArea.getTop() + childArea.getHeight() - (mainArea.getTop() + mainArea.getHeight());
        }
        return 0;
    }

}
