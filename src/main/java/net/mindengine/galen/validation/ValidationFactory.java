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
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.validation.specs.SpecValidationAbsent;
import net.mindengine.galen.validation.specs.SpecValidationContains;
import net.mindengine.galen.validation.specs.SpecValidationHeight;
import net.mindengine.galen.validation.specs.SpecValidationHorizontally;
import net.mindengine.galen.validation.specs.SpecValidationInside;
import net.mindengine.galen.validation.specs.SpecValidationNear;
import net.mindengine.galen.validation.specs.SpecValidationVertically;
import net.mindengine.galen.validation.specs.SpecValidationWidth;

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
        validations.put(SpecNear.class, SpecValidationNear.class);
        validations.put(SpecWidth.class, SpecValidationWidth.class);
        validations.put(SpecHeight.class, SpecValidationHeight.class);
        validations.put(SpecHorizontally.class, SpecValidationHorizontally.class);
        validations.put(SpecVertically.class, SpecValidationVertically.class);
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
