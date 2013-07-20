package net.mindengine.galen.validation.specs;

import static java.lang.String.format;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.SpecRange;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;

public abstract class SpecValidationSize<T extends SpecRange> extends SpecValidation<T> {

    public SpecValidationSize(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    public ValidationError check(String objectName, T spec) {
        PageElement mainObject = getPageElement(objectName);
        
        ValidationError error = checkAvailability(mainObject, objectName);
        if (error != null) {
            return error;
        }
        
        int realValue = getSizeValue(mainObject);
        
        Range range = spec.getRange();
        if (range == null) {
            return error("The spec is incorrect: missing range");
        }
        if (!range.holds(realValue)) {
            if (range.isExact()) {
                return new ValidationError(mainObject.getArea(), format("\"%s\" %s is %dpx instead of %dpx", objectName, getUnitName(), realValue, range.getFrom()));
            }
            else return new ValidationError(mainObject.getArea(), format("\"%s\" %s is %dpx which is not in range of %dpx to %dpx", objectName, getUnitName(), realValue, range.getFrom(), range.getTo()));
        }
        
        return null;
    }

    protected abstract String getUnitName();

    protected abstract int getSizeValue(PageElement element);

}
