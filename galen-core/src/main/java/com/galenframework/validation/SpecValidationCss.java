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
package com.galenframework.validation;

import com.galenframework.page.Rect;
import com.galenframework.specs.SpecCss;
import com.galenframework.validation.specs.SpecValidationText;
import com.galenframework.page.PageElement;

import static java.util.Arrays.asList;

/**
 * Created by ishubin on 2014/11/08.
 */
public class SpecValidationCss extends SpecValidationText<SpecCss> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecCss spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);

        checkAvailability(mainObject, objectName);

        Rect area = mainObject.getArea();
        String realText = mainObject.getCssProperty(spec.getCssPropertyName());
        if (realText == null) {
            realText = "";
        }

        checkValue(spec, objectName, realText, "css property \"" + spec.getCssPropertyName() + "\"", area);

        return new ValidationResult(spec, asList(new ValidationObject(area, objectName)));
    }

}
