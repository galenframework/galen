/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.galenframework.specs.SpecRange;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;

import java.util.List;

public abstract class SpecValidationSize<T extends SpecRange> extends SpecValidation<T> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        double realValue = getSizeValue(mainObject);
        
        double convertedValue = pageValidation.convertValue(spec.getRange(), realValue);

        List<ValidationObject> validationObjects = asList(new ValidationObject(mainObject.getArea(), objectName));

        if (!spec.getRange().holds(convertedValue)) {
                throw new ValidationErrorException()
                    .withValidationObjects(validationObjects)
                    .withMessage(format("\"%s\" %s is %s",
                            objectName,
                            getUnitName(),
                            getReadableRangeAndValue(spec.getRange(), realValue, convertedValue, pageValidation)));
        }

        return new ValidationResult(validationObjects);
    }

    protected abstract String getUnitName();

    protected abstract int getSizeValue(PageElement element);

}
