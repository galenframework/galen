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

import static com.galenframework.validation.ValidationUtils.joinErrorMessagesForObject;
import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import com.galenframework.page.Rect;
import com.galenframework.specs.*;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;

/**
 * Used for specs 'inside' and 'near'
 * @author ishubin
 *
 * @param <T>
 */
public abstract class SpecValidationGeneral<T extends SpecComplex> extends SpecValidation<T> implements ValidationUtils.OffsetProvider {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        
        List<String> messages = new LinkedList<>();

        for (Location location : spec.getLocations()) {
            String message = ValidationUtils.verifyLocation(mainArea, secondArea, location, pageValidation, spec, this);
            if (message != null) {
                messages.add(message);
            }
        }


        List<ValidationObject> validationObjects = new LinkedList<>();
        validationObjects.add(new ValidationObject(mainArea, objectName));
        validationObjects.add(new ValidationObject(secondArea, spec.getObject()));

        if (messages.size() > 0) {
        	throw new ValidationErrorException()
                .withMessage(joinErrorMessagesForObject(messages, objectName))
                .withValidationObjects(validationObjects);
        }

        return new ValidationResult(spec, validationObjects);
    }

}
