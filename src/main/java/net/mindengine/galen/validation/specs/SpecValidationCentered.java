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
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecCentered;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public class SpecValidationCentered extends SpecValidation<SpecCentered> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecCentered spec) throws ValidationErrorException {
        PageElement mainObject = getPageElement(pageValidation, objectName);
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = getPageElement(pageValidation, spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        
        int offsetLeft = mainArea.getLeft() - secondArea.getLeft();
        int offsetRight = secondArea.getLeft() + secondArea.getWidth() - mainArea.getLeft() - mainArea.getWidth();
        
        int offsetTop = mainArea.getTop() - secondArea.getTop();
        int offsetBottom = secondArea.getTop() + secondArea.getHeight() - mainArea.getTop() - mainArea.getHeight();
        
        
        try {
            if (spec.getLocation() == SpecCentered.Location.INSIDE) {
                checkCentered(offsetLeft, offsetRight, offsetTop, offsetBottom, objectName, spec, "inside");
            }
            else {
                //Inverting offset for all directions
                checkCentered(-offsetLeft, -offsetRight, -offsetTop, -offsetBottom, objectName, spec, "on");
            }
        }
        catch (ValidationErrorException exception) {
            exception.setErrorAreas(Arrays.asList(new ErrorArea(mainArea, objectName), new ErrorArea(secondArea, spec.getObject())));
            throw exception;
        }

    }

    private void checkCentered(int offsetLeft, int offsetRight, int offsetTop, int offsetBottom, String objectName, SpecCentered spec, String location) throws ValidationErrorException {
        if (spec.getAlignment() == SpecCentered.Alignment.HORIZONTALLY || spec.getAlignment() == SpecCentered.Alignment.ALL) {
            checkCentered(offsetLeft, offsetRight, objectName, spec, location, "horizontally");
        }
        if (spec.getAlignment() == SpecCentered.Alignment.VERTICALLY || spec.getAlignment() == SpecCentered.Alignment.ALL) {
            checkCentered(offsetTop, offsetBottom, objectName, spec, location, "vertically");
        }
    }

    private void checkCentered(int offsetA, int offsetB, String objectName, SpecCentered spec, String location, String alignment) throws ValidationErrorException {
        int offset = Math.abs(offsetA - offsetB);
        if (offset > spec.getErrorRate()) {
            throw new ValidationErrorException(String.format("\"%s\" is not centered %s %s \"%s\". Offset is %dpx", objectName, alignment, location, spec.getObject(), offset));
        }
        
        if (offsetA < 0 || offsetB < 0){
            throw new ValidationErrorException(String.format("\"%s\" is centered but not %s %s \"%s\"", objectName, alignment, location, spec.getObject(), offset));
        }
    }

    
}
