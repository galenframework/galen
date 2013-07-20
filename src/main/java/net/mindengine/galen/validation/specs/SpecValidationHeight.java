package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.validation.PageValidation;

public class SpecValidationHeight extends SpecValidationSize<SpecHeight>{

    public SpecValidationHeight(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    protected String getUnitName() {
        return "height";
    }

    @Override
    protected int getSizeValue(PageElement element) {
        return element.getArea().getHeight();
    }

}
