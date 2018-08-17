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

import com.galenframework.page.Rect;
import com.galenframework.specs.Side;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;
import com.galenframework.specs.SpecDirectionPosition;

import java.util.List;

import static com.galenframework.specs.Side.*;
import static java.util.Arrays.asList;

public class SpecValidationDirectionPosition extends SpecValidation<SpecDirectionPosition> {

	public enum Direction {
		ABOVE("above", BOTTOM, TOP),
        BELOW("below", TOP, BOTTOM),
        LEFT_OF("left of", RIGHT, LEFT),
        RIGHT_OF("right of", LEFT, RIGHT);
        private final String reportingName;
        private final Side firstEdge;
        private final Side secondEdge;

        Direction(String reportingName, Side firstEdge, Side secondEdge) {
            this.reportingName = reportingName;
            this.firstEdge = firstEdge;
            this.secondEdge = secondEdge;
        }

        public String toString() {
            return this.reportingName;
		}
	}

	private Direction direction;

	public SpecValidationDirectionPosition(Direction direction) {
		this.direction = direction;
	}

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecDirectionPosition spec) throws ValidationErrorException {

        PageElement mainObject = pageValidation.findPageElement(objectName);

        checkAvailability(mainObject, objectName);

        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());

        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();


        List<ValidationObject> objects = asList(
                new ValidationObject(mainArea, objectName),
                new ValidationObject(secondArea, spec.getObject()));

        SimpleValidationResult svr = MetaBasedValidation.forObjectsWithRange(objectName, spec.getObject(), spec.getRange())
                .withFirstEdge(this.direction.firstEdge)
                .withSecondEdge(this.direction.secondEdge)
                .withInvertedCalculation(this.direction == Direction.LEFT_OF || this.direction == Direction.ABOVE)
                .validate(mainArea, secondArea, pageValidation, this.direction.toString());

        if (svr.isError()) {
            throw new ValidationErrorException().withMessage(
                    String.format("\"%s\" is %s \"%s\" %s",
                            objectName,
                            svr.getError(),
                            spec.getObject(),
                            spec.getRange().getErrorMessageSuffix()))
                    .withValidationObjects(objects).withMeta(asList(svr.getMeta()));
        }

        return new ValidationResult(spec, objects).withMeta(asList(svr.getMeta()));
    }
}
