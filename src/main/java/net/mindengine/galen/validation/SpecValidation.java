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

import static java.lang.String.format;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.Locator;

public abstract class SpecValidation<T extends Spec> {
    
    protected static final String OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC = "Cannot find locator for \"%s\" in page spec";
    protected static final String OBJECT_S_IS_ABSENT_ON_PAGE = "\"%s\" is absent on page";
    protected static final String OBJECT_S_IS_NOT_VISIBLE_ON_PAGE = "\"%s\" is not visible on page";

    /**
     * Checks if object satisfies the specified spec
     * @param objectName
     * @param spec
     * @return error with a message. If object satisfies the provided spec then a null is returned
     */
    public abstract ValidationError check(PageValidation pageValidation, String objectName, T spec);
    
    protected ValidationError error(String errorMessage) {
        return new ValidationError().withMessage(errorMessage);
    }
    
    protected ValidationError errorObjectMissingInSpec(String objectName) {
        return error(String.format(OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC, objectName));
    }
    
    protected PageElement getPageElement(PageValidation pageValidation, String objectName) {
        Locator objectLocator = pageValidation.getPageSpec().getObjectLocator(objectName);
        if (objectLocator != null) {
            return pageValidation.getPage().getObject(objectName, objectLocator);
        }
        else return null;
    }
    
    protected ValidationError checkAvailability(PageElement object, String objectName) {
        if (object == null) {
            return errorObjectMissingInSpec(objectName);
        }
        if (!object.isPresent()) {
            return error(format(OBJECT_S_IS_ABSENT_ON_PAGE, objectName));
        }
        else if (!object.isVisible()) {
            return error(format(OBJECT_S_IS_ABSENT_ON_PAGE, objectName));
        }
        else return null;
    }
    
}
