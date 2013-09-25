package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.SpecDirectionPosition;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public class SpecValidationDirectionPosition extends SpecValidation<SpecDirectionPosition> {
	
	public enum Direction {
		ABOVE, BELOW;
		
		public String toString() {
			switch(this) {
			case ABOVE:
				return "above";
			case BELOW:
				return "below";
			}
			return null;
		}
	}

	private Direction direction;

	public SpecValidationDirectionPosition(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void check(PageValidation pageValidation,
			String objectName, SpecDirectionPosition spec) throws ValidationErrorException {
		
		PageElement mainObject = getPageElement(pageValidation, objectName);
        
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = getPageElement(pageValidation, spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        int offset = getOffset(mainArea, secondArea);
        
        
        Range range = convertRange(spec.getRange(), pageValidation);
        
        if (!range.holds(offset)) {
        	throw new ValidationErrorException().withMessage(
        			String.format("\"%s\" is %dpx %s \"%s\" %s", 
        					objectName, 
        					offset, 
        					direction.toString(), 
        					spec.getObject(), 
        					rangeErrorText(spec.getRange())))
        		.withErrorArea(new ErrorArea(mainArea, objectName));
        }
	}

	
	protected String rangeErrorText(Range range) {
		if (range.isExact()) {
			return String.format("instead of %s", range.prettyString());
		}
		else {
			return String.format("which is not in range of %s", range.prettyString());
		}
	}

	private int getOffset(Rect mainArea, Rect secondArea) {
		if (direction == Direction.ABOVE) {
			return secondArea.getTop() - mainArea.getTop() - mainArea.getHeight();
		}
		else {
			return mainArea.getTop() - secondArea.getTop() - secondArea.getHeight();
		}
	}

}
