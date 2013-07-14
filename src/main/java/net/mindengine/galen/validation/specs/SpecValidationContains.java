package net.mindengine.galen.validation.specs;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.Point;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;

public class SpecValidationContains extends SpecValidation<SpecContains> {

    private static final ValidationError NO_ERROR = null;

    public SpecValidationContains(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    public ValidationError check(String objectName, SpecContains spec) {
        Rect objectArea = getObjectArea(objectName);
        
        if (objectArea != null) {
            List<String> erroredObjects = new LinkedList<String>();
            for (String childObjectName : spec.getChildObjects()) {
               Rect childObjectArea = getObjectArea(childObjectName);
               if (childObjectArea != null) {
                   if (!childObjectMatches(spec, objectArea, childObjectArea)) {
                       erroredObjects.add(childObjectName);
                   }
               }
               else {
                   return errorObjectMissingInSpec(childObjectName);
               }
            }
            
            if (erroredObjects.size() > 0) {
                return errorForObjects(erroredObjects).withArea(objectArea);
            }
            else return NO_ERROR;
        }        
        else return errorObjectMissingInSpec(objectName);
    }

    private ValidationError errorForObjects(List<String> erroredObjects) {
        if (erroredObjects.size() == 1) {
            return error(format("Object \"%s\" is outside the specified element", erroredObjects.get(0)));
        }
        else {
            return error(format("Objects %s are outside the specified element", commaSeparatedObjects(erroredObjects)));
        }
    }

    private Object commaSeparatedObjects(List<String> erroredObjects) {
        StringBuffer buffer = new StringBuffer();
        boolean comma = false;
        
        for (String objectName : erroredObjects) {
            if (comma) {
                buffer.append(", ");
            }
            buffer.append("\"");
            buffer.append(objectName);
            buffer.append("\"");
            comma = true;
        }
        return buffer.toString();
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
