package net.mindengine.galen.validation.specs;

import static java.lang.String.format;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;

public class SpecValidationAbsent extends SpecValidation<SpecAbsent>{

    public SpecValidationAbsent(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    public ValidationError check(String objectName, SpecAbsent spec) {
        PageElement mainObject = getPageElement(objectName);
        if (mainObject == null) {
            return errorObjectMissingInSpec(objectName);
        }
        else if (mainObject.isPresent() && mainObject.isVisible()) {
            return new ValidationError(mainObject.getArea(), format("Object \"%s\" is not absent on page", objectName));
        }
        else return null;
    }

}
