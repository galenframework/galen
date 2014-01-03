/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.SpecRange;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public abstract class SpecValidationSize<T extends SpecRange> extends SpecValidation<T> {

    @Override
    public void check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        int realValue = getSizeValue(mainObject);
        
        Range range = convertRange(spec.getRange(), pageValidation);
        
        if (!range.holds(realValue)) {
                throw new ValidationErrorException()
                    .withErrorArea(new ErrorArea(mainObject.getArea(), objectName))
                    .withMessage(format("\"%s\" %s is %dpx %s", objectName, getUnitName(), realValue, range.getErrorMessageSuffix()));
        }
    }

    protected abstract String getUnitName();

    protected abstract int getSizeValue(PageElement element);

}
