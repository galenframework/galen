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

import com.galenframework.page.PageElement;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.*;
import com.galenframework.validation.*;

import java.util.LinkedList;
import java.util.List;

import static com.galenframework.validation.ValidationUtils.joinErrorMessagesForObject;
import static com.galenframework.validation.ValidationUtils.joinMessages;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SpecValidationInside extends SpecValidation<SpecInside> {


    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecInside spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());

        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();

        List<ValidationObject> objects = asList(new ValidationObject(mainArea, objectName),new ValidationObject(secondArea, spec.getObject()));

        checkIfCompletelyInside(objectName, spec, mainArea, secondArea, objects);
        List<LayoutMeta> layoutMeta = verifyAllSides(pageValidation, objectName, mainArea, secondArea, spec, objects);

        return new ValidationResult(spec, objects).withMeta(layoutMeta);
    }

    private List<LayoutMeta> verifyAllSides(PageValidation pageValidation, String objectName, Rect mainArea, Rect secondArea, SpecInside spec, List<ValidationObject> validationObjects) throws ValidationErrorException {
        List<LayoutMeta> meta = new LinkedList<>();

        List<String> errorMessages = new LinkedList<>();
        for (Location location : spec.getLocations()) {
            Range range = location.getRange();

            List<String> perLocationErrors = new LinkedList<>();

            for (Side side : location.getSides()) {
                SimpleValidationResult svr = MetaBasedValidation.forObjectsWithRange(objectName, spec.getObject(), range)
                        .withBothEdges(side)
                        .withInvertedCalculation(side == Side.RIGHT || side == Side.BOTTOM)
                        .validate(mainArea, secondArea, pageValidation, side);
                meta.add(svr.getMeta());

                if (svr.isError()) {
                    perLocationErrors.add(svr.getError());
                }
            }

            if (!perLocationErrors.isEmpty()) {
                errorMessages.add(format("%s %s", joinMessages(perLocationErrors, " and "), range.getErrorMessageSuffix()));
            }

        }

        if (errorMessages.size() > 0) {
            throw new ValidationErrorException()
                    .withMessage(joinErrorMessagesForObject(errorMessages, objectName))
                    .withValidationObjects(validationObjects)
                    .withMeta(meta);
        }
        return meta;
    }

    private void checkIfCompletelyInside(String objectName, SpecInside spec, Rect mainArea, Rect secondArea, List<ValidationObject> objects) throws ValidationErrorException {
        if (!spec.getPartly()) {
            Point[] points = mainArea.getPoints();

            int maxOffset = 0;
            for (Point point : points) {
                int offset = secondArea.calculatePointOffsetDistance(point);
                if (maxOffset < offset) {
                    maxOffset = offset;
                }
            }
            if (maxOffset > 2) {
                throw new ValidationErrorException()
                        .withValidationObjects(objects)
                        .withMessage(format("\"%s\" is not completely inside. The offset is %dpx.", objectName, maxOffset));
            }
        }
    }

}
