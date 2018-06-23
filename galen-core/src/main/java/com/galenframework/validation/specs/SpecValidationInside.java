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

import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import com.galenframework.specs.*;
import com.galenframework.validation.*;

import java.util.List;

import static java.lang.String.format;

public class SpecValidationInside extends SpecValidationComplex<SpecInside> {

    @Override
    protected SimpleValidationResult validateSide(String objectName, SpecInside spec, Range range, Side side, Rect mainArea, Rect secondArea, PageValidation pageValidation) {
        return MetaBasedValidation.forObjectsWithRange(objectName, spec.getObject(), range)
                .withBothEdges(side)
                .withInvertedCalculation(side == Side.RIGHT || side == Side.BOTTOM)
                .validate(mainArea, secondArea, pageValidation, side);
    }

    @Override
    protected void doCustomValidations(String objectName, Rect mainArea, Rect secondArea, SpecInside spec, List<ValidationObject> objects) throws ValidationErrorException {
        checkIfCompletelyInside(objectName, spec, mainArea, secondArea, objects);
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
