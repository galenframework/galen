package net.mindengine.galen.validation;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecCss;
import net.mindengine.galen.validation.specs.SpecValidationText;

/**
 * Created by ishubin on 2014/11/08.
 */
public class SpecValidationCss extends SpecValidationText<SpecCss> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecCss spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);

        checkAvailability(mainObject, objectName);

        Rect area = mainObject.getArea();
        String realText = mainObject.getCssProperty(spec.getCssPropertyName());
        if (realText == null) {
            realText = "";
        }

        checkValue(spec, objectName, realText, "css property \"" + spec.getCssPropertyName() + "\"", area);
    }

}
