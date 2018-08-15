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

import static java.lang.String.format;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.page.PageElement;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import com.galenframework.specs.SpecContains;
import com.galenframework.validation.*;

public class SpecValidationContains extends SpecValidation<SpecContains> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecContains spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);
        
        Rect objectArea = mainObject.getArea();
        List<String> allFoundObjects = findChildObjects(pageValidation, spec);
        List<String> errorMessages = new LinkedList<>();

        List<ValidationObject> objects = new LinkedList<>();
        objects.add(new ValidationObject(objectArea, objectName));
        
        for (String childObjectName : allFoundObjects) {
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


                    objects.add(new ValidationObject(childObjectArea, childObjectName));

                    if (!childObjectMatches(spec, objectArea, childObjectArea)) {
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
            throw new ValidationErrorException(objects, errorMessages);
        }

        return new ValidationResult(spec, objects);
    }

    private List<String> findChildObjects(PageValidation pageValidation, SpecContains spec) throws ValidationErrorException {
        List<String> allFoundObjects = new LinkedList<>();
        for (String objectStatement : spec.getChildObjects()) {
            List<String> objects = pageValidation.getPageSpec().findAllObjectsMatchingStrictStatements(objectStatement);
            if (objects.isEmpty()) {
                throw new ValidationErrorException("There are no objects matching: " + objectStatement);
            }
            allFoundObjects.addAll(objects);
        }
        return allFoundObjects;
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
