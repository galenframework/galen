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
package com.galenframework.validation.specs;

import com.galenframework.page.Rect;
import com.galenframework.parser.SyntaxException;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;
import com.galenframework.specs.SpecDirectionPosition;

import java.util.List;

import static java.util.Arrays.asList;

public class SpecValidationDirectionPosition extends SpecValidation<SpecDirectionPosition> {
	
	public enum Direction {
		ABOVE("above"),
        BELOW("below"),
        LEFT_OF("left of"),
        RIGHT_OF("right of");
        private final String reportingName;

        Direction(String reportingName) {
            this.reportingName = reportingName;
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
        int offset = getOffset(mainArea, secondArea);
        

        List<ValidationObject> objects = asList(
                new ValidationObject(mainArea, objectName),
                new ValidationObject(secondArea, spec.getObject()));


        double convertedOffset = pageValidation.convertValue(spec.getRange(), offset);

        if (!spec.getRange().holds(convertedOffset)) {
        	throw new ValidationErrorException().withMessage(
                    String.format("\"%s\" is %dpx %s \"%s\" %s",
                            objectName,
                            offset,
                            direction.toString(),
                            spec.getObject(),
                            spec.getRange().getErrorMessageSuffix()))
        		.withValidationObjects(objects);
        }

        return new ValidationResult(objects);
	}




    private int getOffset(Rect mainArea, Rect secondArea) {
		if (direction == Direction.ABOVE) {
			return secondArea.getTop() - mainArea.getTop() - mainArea.getHeight();
		}
        else if (direction == Direction.BELOW) {
			return mainArea.getTop() - secondArea.getTop() - secondArea.getHeight();
		}
        else if (direction == Direction.LEFT_OF) {
            return secondArea.getLeft() - mainArea.getLeft() - mainArea.getWidth();
        }
        else if (direction == Direction.RIGHT_OF) {
            return mainArea.getLeft() - secondArea.getLeft() - secondArea.getWidth();
        }
        else {
            throw new SyntaxException("Unknown direction: " + direction.name());
        }
	}

}
