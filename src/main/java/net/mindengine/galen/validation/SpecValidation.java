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

import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Spec;

public abstract class SpecValidation<T extends Spec> {
    
    private PageValidation pageValidation;

    public SpecValidation(PageValidation pageValidation) {
        this.setPageValidation(pageValidation);
    }

    /**
     * Checks if object satisfies the specified spec
     * @param objectName
     * @param spec
     * @return error with a message. If object satisfies the provided spec then a null is returned
     */
    public abstract ValidationError check(String objectName, T spec);

    public PageValidation getPageValidation() {
        return pageValidation;
    }

    public void setPageValidation(PageValidation pageValidation) {
        this.pageValidation = pageValidation;
    }
    
    protected ValidationError error(String errorMessage) {
        return new ValidationError(errorMessage);
    }
    
    protected ValidationError errorObjectMissingInSpec(String objectName) {
        return error("Object with name \"" + objectName + "\" is not defined in page spec");
    }
    
    protected Rect getObjectArea(String objectName) {
        return getPageValidation().getObjectArea(objectName);
    }

}
