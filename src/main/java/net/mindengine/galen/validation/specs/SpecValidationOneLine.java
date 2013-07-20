package net.mindengine.galen.validation.specs;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecObjectsOnOneLine;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;

public abstract class SpecValidationOneLine<T extends SpecObjectsOnOneLine> extends SpecValidation<T> {

    public SpecValidationOneLine(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    public ValidationError check(String objectName, T spec) {
        PageElement mainObject = getPageElement(objectName);
        
        ValidationError error = checkAvailability(mainObject, objectName);
        if (error != null) {
            return error;
        }
        
        List<String> misalignedObjectNames = new LinkedList<String>();
        Rect area = null;
        
        for (String childObjectName : spec.getChildObjects()) {
            PageElement childObject = getPageElement(childObjectName);
            error = checkAvailability(childObject, childObjectName);
            if (error != null) {
                return error;
            }
            else if (Math.abs(getOffset(spec, mainObject, childObject)) > 1) {
                misalignedObjectNames.add(childObjectName);
                if (area != null) {
                    area = Rect.boundaryOf(area, childObject.getArea());
                }
                else area = childObject.getArea();
            }
        }
        
        if (misalignedObjectNames.size() > 0) {
            return errorMisalignedObjects(area, objectName, misalignedObjectNames, spec);
        }
        else return null;
    }

    private ValidationError errorMisalignedObjects(Rect area,String objectName, List<String> misalignedObjectNames, T spec) {
        String pattern = null;
        if (misalignedObjectNames.size() > 1) {
            pattern = "%s are not aligned %s with \"%s\"";
        }
        else {
            pattern = "%s is not aligned %s with \"%s\"";
        }
        return new ValidationError(area, String.format(pattern, convertObjectNameToCommaSeparated(misalignedObjectNames), getAligmentText(spec), objectName));
    }

    private String convertObjectNameToCommaSeparated(List<String> misalignedObjectNames) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (String objectName : misalignedObjectNames) {
            if (!first) {
                buffer.append(", ");
            }
            else first = false;
            buffer.append("\"");
            buffer.append(objectName);
            buffer.append("\"");
        }
        return buffer.toString();
    }

    protected abstract String getAligmentText(T spec);

    protected abstract int getOffset(T spec, PageElement mainObject, PageElement childObject);

}
