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

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Point;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public class SpecValidationContains extends SpecValidation<SpecContains> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecContains spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        Rect objectArea = mainObject.getArea();
        
        List<String> childObjects = fetchChildObjets(spec.getChildObjects(), pageValidation.getPageSpec());

        
        List<ErrorArea> errorAreas = new LinkedList<ErrorArea>();
        List<String> errorMessages = new LinkedList<String>();
        
        for (String childObjectName : childObjects) {
            PageElement childObject = pageValidation.findPageElement(childObjectName);
            if (childObject != null) {
                if (!childObject.isPresent()) {
                    throw new ValidationErrorException()
                        .withMessage(format(OBJECT_S_IS_ABSENT_ON_PAGE, childObjectName));
                }
                else if (!childObject.isVisible()) {
                    throw new ValidationErrorException()
                        .withMessage(format(OBJECT_S_IS_NOT_VISIBLE_ON_PAGE, childObjectName));
                } 
                else {
                    Rect childObjectArea = childObject.getArea();
                    if (!childObjectMatches(spec, objectArea, childObjectArea)) {
                        errorAreas.add(new ErrorArea(childObjectArea, childObjectName));
                        errorMessages.add(format("\"%s\" is outside \"%s\"", childObjectName, objectName));
                    }
                }
            }
            else {
                throw new ValidationErrorException()
                    .withMessage(format(OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC, childObjectName));
            }
        }
        
        if (errorMessages.size() > 0 ) { 
            throw new ValidationErrorException(errorAreas, errorMessages).withErrorArea(new ErrorArea(objectArea, objectName));
        }
    }

    
    private boolean childObjectMatches(SpecContains spec, Rect objectArea, Rect childObjectArea) {
        int matchingPoints = findMatchingPoints(objectArea, childObjectArea);
        
        if (spec.isPartly()) {
            return matchingPoints > 0;
        }
        else return matchingPoints == 4;
    }

    private int findMatchingPoints(Rect objectArea, Rect childObjectArea) {
        Point[] childPoints = childObjectArea.getPoints();
        int matchingPoints = 0;
        for (Point point : childPoints) {
            if (objectArea.contains(point)) {
                matchingPoints++;
            }
        }
        return matchingPoints;
    }
}
