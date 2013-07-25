package net.mindengine.galen.validation;

import net.mindengine.galen.specs.Spec;

public interface ValidationListener {

    void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error);

    void onOnObjectCheck(PageValidation pageValidation, String objectName, Spec spec);

    

}
