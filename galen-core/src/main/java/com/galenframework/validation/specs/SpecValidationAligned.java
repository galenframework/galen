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
package com.galenframework.validation.specs;

import java.util.List;

import com.galenframework.page.PageElement;
import com.galenframework.validation.*;
import com.galenframework.specs.SpecAligned;

import static java.util.Arrays.asList;

public abstract class SpecValidationAligned<T extends SpecAligned> extends SpecValidation<T> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        PageElement childObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(childObject, spec.getObject());
        
        int offset = Math.abs(getOffset(spec, mainObject, childObject));

        List<ValidationObject> objects = asList(new ValidationObject(mainObject.getArea(), objectName), new ValidationObject(childObject.getArea(), spec.getObject()));

        if (offset > Math.abs(spec.getErrorRate())) {
            throw new ValidationErrorException(objects,
                    asList(errorMisalignedObjects(objectName, spec.getObject(), spec, offset)));
        }

        return new ValidationResult(spec, objects);
    }

    private String errorMisalignedObjects(String objectName, String misalignedObjectName, T spec, int offset) {
        return (String.format("\"%s\" is not aligned %s with \"%s\". Offset is %dpx", misalignedObjectName, getAligmentText(spec), objectName, offset));
    }

    protected abstract String getAligmentText(T spec);

    protected abstract int getOffset(T spec, PageElement mainObject, PageElement childObject);

}
