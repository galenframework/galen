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

import static java.lang.String.copyValueOf;
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
public abstract class SpecValidationGeneral<T extends SpecComplex> extends SpecValidation<T> {

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
            String message = verifyLocation(mainArea, secondArea, location, pageValidation, spec);
            if (message != null) {
                messages.add(message);
            }
        }


        List<ValidationObject> validationObjects = new LinkedList<>();
        validationObjects.add(new ValidationObject(mainArea, objectName));
        validationObjects.add(new ValidationObject(secondArea, spec.getObject()));

        if (messages.size() > 0) {
        	throw new ValidationErrorException()
                .withMessage(createMessage(messages, objectName))
                .withValidationObjects(validationObjects);
        }

        return new ValidationResult(spec, validationObjects);
    }

    private String createMessage(List<String> messages, String objectName) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(format("\"%s\" ", objectName));
        boolean comma = false;
        for (String message : messages) {
            if (comma) {
                buffer.append(", ");
            }
            buffer.append("is ");
            buffer.append(message);
            comma = true;
        }
        return buffer.toString();
    }

    protected String verifyLocation(Rect mainArea, Rect secondArea, Location location, PageValidation pageValidation, T spec) {
        List<String> messages = new LinkedList<>();


        Range range = location.getRange();

        for (Side side : location.getSides()) {
            int offset = getOffsetForSide(mainArea, secondArea, side, spec);
            double calculatedOffset = pageValidation.convertValue(range, offset);

            if (!range.holds(calculatedOffset)) {
                if (range.isPercentage()) {
                    int precision = range.findPrecision();

                    messages.add(String.format("%s%% [%dpx] %s", new RangeValue(calculatedOffset, precision).toString(), offset, side));
                } else {
                    messages.add(format("%dpx %s", offset, side));
                }
            }
        }
        
        if (messages.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            boolean comma = false;
            for (String message : messages) {
                if (comma) {
                    buffer.append(" and ");
                }
                buffer.append(message);
                comma = true;
            }
            
            buffer.append(' ');
            buffer.append(range.getErrorMessageSuffix());
            if (range.isPercentage()) {
                int objectValue = pageValidation.getObjectValue(range.getPercentageOfValue());
                buffer.append(' ');
                buffer.append(rangeCalculatedFromPercentage(range, objectValue));
            }
            return buffer.toString();
        }
        else return null;
    }

    protected abstract int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, T spec);
    
}
