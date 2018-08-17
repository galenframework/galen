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

import java.util.List;

import com.galenframework.page.Rect;
import com.galenframework.specs.SpecCentered;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;

import static java.util.Arrays.asList;

public class SpecValidationCentered extends SpecValidation<SpecCentered> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecCentered spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        
        int offsetLeft = mainArea.getLeft() - secondArea.getLeft();
        int offsetRight = secondArea.getLeft() + secondArea.getWidth() - mainArea.getLeft() - mainArea.getWidth();
        
        int offsetTop = mainArea.getTop() - secondArea.getTop();
        int offsetBottom = secondArea.getTop() + secondArea.getHeight() - mainArea.getTop() - mainArea.getHeight();


        List<ValidationObject> objects = asList(new ValidationObject(mainArea, objectName), new ValidationObject(secondArea, spec.getObject()));
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
            exception.setValidationObjects(objects);
            throw exception;
        }

        return new ValidationResult(spec, objects);
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
