/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.validation;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecCss;
import net.mindengine.galen.validation.specs.SpecValidationText;

/**
 * Created by ishubin on 2014/11/08.
 */
public class SpecValidationCss extends SpecValidationText<SpecCss> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecCss spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);

        checkAvailability(mainObject, objectName);

        Rect area = mainObject.getArea();
        String realText = mainObject.getCssProperty(spec.getCssPropertyName());
        if (realText == null) {
            realText = "";
        }

        checkValue(spec, objectName, realText, "css property \"" + spec.getCssPropertyName() + "\"", area);
    }

}
