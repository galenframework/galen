package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.validation.PageValidation;

public class SpecValidationWidth extends SpecValidationSize<SpecWidth>{

    public SpecValidationWidth(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    protected String getUnitName() {
        return "width";
    }

    @Override
    protected int getSizeValue(PageElement element) {
        return element.getArea().getWidth();
    }

}
