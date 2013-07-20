package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.validation.PageValidation;

public class SpecValidationVertically extends SpecValidationOneLine<SpecVertically> {

    public SpecValidationVertically(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    protected String getAligmentText(SpecVertically spec) {
        return String.format("vertically %s", spec.getAlignment().toString());
    }

    @Override
    protected int getOffset(SpecVertically spec, PageElement mainObject, PageElement childObject) {
        Rect mainArea = mainObject.getArea();
        Rect childArea = childObject.getArea();
        
        switch(spec.getAlignment()) {
        case CENTERED:
            return childArea.getLeft() + (childArea.getWidth() / 2) - (mainArea.getLeft() + (mainArea.getWidth() / 2)); 
        case LEFT:
            return childArea.getLeft() - mainArea.getLeft();
        case RIGHT:
            return childArea.getLeft() + childArea.getWidth() - (mainArea.getLeft() + mainArea.getWidth());
        }
        return 0;
    }

}
