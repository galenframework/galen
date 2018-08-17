/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation;

import static com.galenframework.validation.ValidationUtils.rangeCalculatedFromPercentage;
import static java.lang.String.format;


import com.galenframework.config.GalenConfig;
import com.galenframework.page.PageElement;
import com.galenframework.specs.Range;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.Spec;

public abstract class SpecValidation<T extends Spec> {
    
    protected static final String OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC = "Cannot find locator for \"%s\" in page spec";
    protected static final String OBJECT_S_IS_ABSENT_ON_PAGE = "\"%s\" is absent on page";
    protected static final String OBJECT_S_IS_NOT_VISIBLE_ON_PAGE = "\"%s\" is not visible on page";


    /**
     * Checks if object satisfies the specified spec
     * @param objectName
     * @param spec
     * @throws ValidationErrorException
     */
    public abstract ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException;
    
    
    protected void checkAvailability(PageElement object, String objectName) throws ValidationErrorException {
        if (GalenConfig.getConfig().shouldCheckVisibilityGlobally()) {
            if (object == null) {
                throw new ValidationErrorException(format(OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC, objectName));
            }
            if (!object.isPresent()) {
                throw new ValidationErrorException(format(OBJECT_S_IS_ABSENT_ON_PAGE, objectName));
            } else if (!object.isVisible()) {
                throw new ValidationErrorException((format(OBJECT_S_IS_NOT_VISIBLE_ON_PAGE, objectName)));
            }
        }
    }

    protected String getReadableRangeAndValue(Range range, double realValue, double convertedValue, PageValidation pageValidation) {
        if (range.isPercentage()) {
            int objectValue = pageValidation.getObjectValue(range.getPercentageOfValue());

            return format("%s%% [%dpx] %s %s",
                    new RangeValue(convertedValue, range.findPrecision()).toString(),
                    (int)realValue,
                    range.getErrorMessageSuffix(),
                    rangeCalculatedFromPercentage(range, objectValue));
        } else {
            return format("%spx %s",
                    new RangeValue(realValue, range.findPrecision()).toString(),
                    range.getErrorMessageSuffix());
        }
    }

}
