/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import org.apache.commons.collections.CollectionUtils;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Point;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.validation.*;

public class SpecValidationContains extends SpecValidation<SpecContains> {

    @Override
    public ValidationResult check(final PageValidation pageValidation, final String objectName, final SpecContains spec) throws ValidationErrorException {
        final PageElement mainObject = pageValidation.findPageElement(objectName);

        checkAvailability(mainObject, objectName);

        final Rect objectArea = mainObject.getArea();

        final List<String> childObjects = fetchChildObjets(spec.getChildObjects(), pageValidation.getPageSpec());

        final List<String> errorMessages = new LinkedList<String>();

        final List<ValidationObject> objects = new LinkedList<ValidationObject>();
        objects.add(new ValidationObject(objectArea, objectName));

        for (final String childObjectName : childObjects) {
            final PageElement childObject = pageValidation.findPageElement(childObjectName);
            if (childObject != null) {
                if (!childObject.isPresent()) {
                    throw new ValidationErrorException().withMessage(format(OBJECT_S_IS_ABSENT_ON_PAGE, childObjectName));
                } else if (!childObject.isVisible()) {
                    throw new ValidationErrorException().withMessage(format(OBJECT_S_IS_NOT_VISIBLE_ON_PAGE, childObjectName));
                } else {
                    final Rect childObjectArea = childObject.getArea();

                    objects.add(new ValidationObject(childObjectArea, childObjectName));

                    if (!childObjectMatches(spec, objectArea, childObjectArea)) {
                        errorMessages.add(format("\"%s\" is outside \"%s\"", childObjectName, objectName));
                    }
                }
            } else {
                throw new ValidationErrorException().withMessage(format(OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC, childObjectName));
            }
        }

        if (CollectionUtils.isNotEmpty(errorMessages)) {
            throw new ValidationErrorException(objects, errorMessages).withValidationObject(new ValidationObject(objectArea, objectName));
        }

        return new ValidationResult(objects);
    }

    private boolean childObjectMatches(final SpecContains spec, final Rect objectArea, final Rect childObjectArea) {
        final int matchingPoints = findMatchingPoints(objectArea, childObjectArea);

        if (spec.isPartly()) {
            return matchingPoints > 0;
        } else {
            return matchingPoints == 4;
        }
    }

    private int findMatchingPoints(final Rect objectArea, final Rect childObjectArea) {
        final Point[] childPoints = childObjectArea.getPoints();
        int matchingPoints = 0;
        for (final Point point : childPoints) {
            if (objectArea.contains(point)) {
                matchingPoints++;
            }
        }
        return matchingPoints;
    }
}
