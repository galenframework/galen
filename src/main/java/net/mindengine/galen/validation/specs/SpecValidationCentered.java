package net.mindengine.galen.validation.specs;

import java.util.Arrays;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecCentered;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public class SpecValidationCentered extends SpecValidation<SpecCentered> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecCentered spec) throws ValidationErrorException {
        PageElement mainObject = getPageElement(pageValidation, objectName);
        checkAvailability(mainObject, objectName);
        
        PageElement secondObject = getPageElement(pageValidation, spec.getObject());
        checkAvailability(secondObject, spec.getObject());
        
        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();
        
        int offsetLeft = mainArea.getLeft() - secondArea.getLeft();
        int offsetRight = secondArea.getLeft() + secondArea.getWidth() - mainArea.getLeft() - mainArea.getWidth();
        
        int offsetTop = mainArea.getTop() - secondArea.getTop();
        int offsetBottom = secondArea.getTop() + secondArea.getHeight() - mainArea.getTop() - mainArea.getHeight();
        
        
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
            exception.setErrorAreas(Arrays.asList(new ErrorArea(mainArea, objectName), new ErrorArea(secondArea, spec.getObject())));
            throw exception;
        }

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
        if (offsetA < 0 || offsetB < 0 || Math.abs(offsetA - offsetB) > 2) {
            throw new ValidationErrorException(String.format("\"%s\" is not centered %s %s \"%s\"", objectName, alignment, location, spec.getObject()));
        }
    }

    
}
