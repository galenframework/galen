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
package net.mindengine.galen.validation.specs;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.SpecRange;
import net.mindengine.galen.validation.*;

import java.util.List;

public abstract class SpecValidationSize<T extends SpecRange> extends SpecValidation<T> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        int realValue = getSizeValue(mainObject);
        
        Range range = convertRange(spec.getRange(), pageValidation);

        List<ValidationObject> validationObjects = asList(new ValidationObject(mainObject.getArea(), objectName));

        if (!range.holds(realValue)) {
                throw new ValidationErrorException()
                    .withValidationObjects(validationObjects)
                    .withMessage(format("\"%s\" %s is %s", objectName, getUnitName(), getRangeAndValue(spec.getRange(), range, realValue)));
        }

        return new ValidationResult(validationObjects);
    }

    protected abstract String getUnitName();

    protected abstract int getSizeValue(PageElement element);

}
