/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecInside;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationErrorException;
import com.galenframework.validation.ValidationResult;

import java.util.List;

import static java.util.Arrays.asList;

public class SpecValidationInside extends SpecValidationGeneral<SpecInside> {


    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecInside spec) throws ValidationErrorException {
        super.check(pageValidation, objectName, spec);


        PageElement mainObject = pageValidation.findPageElement(objectName);
        PageElement secondObject = pageValidation.findPageElement(spec.getObject());

        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();

        List<ValidationObject> objects = asList(new ValidationObject(mainArea, objectName),new ValidationObject(secondArea, spec.getObject()));

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
                        .withMessage(String.format("\"%s\" is not completely inside. The offset is %dpx.", objectName, maxOffset));
            }
        }

        return new ValidationResult(spec, objects);
    }

    @Override
    protected int getOffsetForSide(Rect mainArea, Rect parentArea, Side side, SpecInside spec) {
        if (side == Side.LEFT) {
            return mainArea.getLeft() - parentArea.getLeft();
        }
        else if (side == Side.TOP) {
            return mainArea.getTop() - parentArea.getTop();
        }
        else if (side == Side.RIGHT) {
            return parentArea.getLeft() + parentArea.getWidth() - (mainArea.getLeft() + mainArea.getWidth());
        }
        else if (side == Side.BOTTOM) {
            return parentArea.getTop() + parentArea.getHeight() - (mainArea.getTop() + mainArea.getHeight());
        }
        else {
            return 0;
        }
    }

}
