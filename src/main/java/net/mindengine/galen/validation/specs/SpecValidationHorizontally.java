package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.validation.PageValidation;

public class SpecValidationHorizontally extends SpecValidationOneLine<SpecHorizontally> {

    public SpecValidationHorizontally(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    protected String getAligmentText(SpecHorizontally spec) {
        return String.format("horizontally %s", spec.getAlignment().toString());
    }

    @Override
    protected int getOffset(SpecHorizontally spec, PageElement mainObject, PageElement childObject) {
        Rect mainArea = mainObject.getArea();
        Rect childArea = childObject.getArea();
        
        switch(spec.getAlignment()) {
        case CENTERED:
            return childArea.getTop() + (childArea.getHeight() / 2) - (mainArea.getTop() + (mainArea.getHeight() / 2)); 
        case TOP:
            return childArea.getTop() - mainArea.getTop();
        case BOTTOM:
            return childArea.getTop() + childArea.getHeight() - (mainArea.getTop() + mainArea.getHeight());
        }
        return 0;
    }

}
