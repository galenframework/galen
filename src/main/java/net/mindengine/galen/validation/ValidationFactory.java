/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.validation;

import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.validation.specs.SpecValidationAbsent;
import net.mindengine.galen.validation.specs.SpecValidationContains;
import net.mindengine.galen.validation.specs.SpecValidationInside;

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
        validations.put(SpecAbsent.class, SpecValidationAbsent.class);
        validations.put(SpecInside.class, SpecValidationInside.class);
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
