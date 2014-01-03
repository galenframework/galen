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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.SpecComplex;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

/**
 * Used for specs 'inside' and 'near'
 * @author ishubin
 *
 * @param <T>
 */
public abstract class SpecValidationGeneral<T extends SpecComplex> extends SpecValidation<T>{

    @Override
    public void check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        
        List<String> messages = new LinkedList<String>();
        
        for (Location location : spec.getLocations()) {
            String message = verifyLocation(mainArea, secondArea, location, pageValidation, spec);
            if (message != null) {
                messages.add(message);
            }
        }
        
        if (messages.size() > 0) {
        	throw new ValidationErrorException()
                .withErrorArea(new ErrorArea(mainArea, objectName))
                .withErrorArea(new ErrorArea(secondArea, spec.getObject()))
                .withMessage(createMessage(messages, objectName)); 
        }
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
        List<String> messages = new LinkedList<String>();
        Range range;
        
        try {
            range = pageValidation.convertRange(location.getRange());
        }
        catch (Exception ex) {
            return format("Cannot convert range: " + ex.getMessage());
        }
        
        for (Side side : location.getSides()) {
            int offset = getOffsetForSide(mainArea, secondArea, side, spec);
            if (!range.holds(offset)) {
                messages.add(format("%dpx %s", offset, side));
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
            return buffer.toString(); 
        }
        else return null;
    }

    protected abstract int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, T spec);
    
}
