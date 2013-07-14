package net.mindengine.galen.validation;

import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.validation.specs.SpecValidationContains;

public class ValidationFactory {
    
    @SuppressWarnings("rawtypes")
    private Map<Class<? extends Spec>, Class<? extends SpecValidation>> validations = new HashMap<Class<? extends Spec>, Class<? extends SpecValidation>>();
    
    private static ValidationFactory _instance = null;
    
    private ValidationFactory() {
        initValidations();
    }
    
    public synchronized static ValidationFactory get() {
        if (_instance == null) {
            _instance = new ValidationFactory();
        }
        return _instance;
    }
    
    private void initValidations() {
        validations.put(SpecContains.class, SpecValidationContains.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static SpecValidation<? extends Spec> getValidation(Spec spec, PageValidation pageValidation) {
        Class<? extends SpecValidation> validationClass = ValidationFactory.get().validations.get(spec.getClass());
        if (validationClass == null) {
            throw new RuntimeException("There is no known validation for spec " + spec.getClass());
        }
        else {
            try {
                return validationClass.getConstructor(PageValidation.class).newInstance(pageValidation);
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate validation " + validationClass + " for spec " + spec.getClass());
            }
        }
    }

}
