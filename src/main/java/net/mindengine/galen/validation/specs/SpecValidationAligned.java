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

import java.util.Arrays;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecAligned;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public abstract class SpecValidationAligned<T extends SpecAligned> extends SpecValidation<T> {

    @Override
    public void check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = getPageElement(pageValidation, objectName);
        
        checkAvailability(mainObject, objectName);
        
        PageElement childObject = getPageElement(pageValidation, spec.getObject());
        checkAvailability(childObject, spec.getObject());
        
        int offset = Math.abs(getOffset(spec, mainObject, childObject)); 
        if (offset > Math.abs(spec.getErrorRate())) {
            throw new ValidationErrorException(Arrays.asList(new ErrorArea(mainObject.getArea(), objectName), new ErrorArea(childObject.getArea(), spec.getObject())),
                    Arrays.asList(errorMisalignedObjects(objectName, spec.getObject(), spec, offset)));
        }
    }

    private String errorMisalignedObjects(String objectName, String misalignedObjectName, T spec, int offset) {
        return (String.format("\"%s\" is not aligned %s with \"%s\". Offset is %dpx", misalignedObjectName, getAligmentText(spec), objectName, offset));
    }

    protected abstract String getAligmentText(T spec);

    protected abstract int getOffset(T spec, PageElement mainObject, PageElement childObject);

}
