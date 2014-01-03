/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.reader.page.PageSpec;

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
    public abstract void check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException;
    
    
    
    protected void checkAvailability(PageElement object, String objectName) throws ValidationErrorException {
        if (object == null) {
            throw new ValidationErrorException(format(OBJECT_WITH_NAME_S_IS_NOT_DEFINED_IN_PAGE_SPEC, objectName));
        }
        if (!object.isPresent()) {
        	throw new ValidationErrorException(format(OBJECT_S_IS_ABSENT_ON_PAGE, objectName));
        }
        else if (!object.isVisible()) {
        	throw new ValidationErrorException((format(OBJECT_S_IS_NOT_VISIBLE_ON_PAGE, objectName)));
        }
        
    }
    
    /**
     * Fetches all child object, using simple regular expression
     * @param childObjects
     * @param pageSpec
     * @param validatedObject 
     * @return
     * @throws ValidationErrorException
     */
    protected List<String> fetchChildObjets(List<String> childObjects, PageSpec pageSpec) throws ValidationErrorException {
        List<String> resultObjects = new LinkedList<String>();
        
        for (String objectName : childObjects) {
            if (objectName.contains("*")) {
                
                List<String> foundObjects = pageSpec.findMatchingObjectNames(objectName);
                if (foundObjects.size() == 0) {
                    throw new ValidationErrorException("There are no objects matching: " + objectName);
                }
                resultObjects.addAll(foundObjects);
            }
            else {
                resultObjects.add(objectName);
            }
        }
        return resultObjects;
    }
    
    protected Range convertRange(Range range, PageValidation pageValidation) throws ValidationErrorException {
    	try {
            return pageValidation.convertRange(range);
        }
        catch (Exception ex) {
            throw new ValidationErrorException(format("Cannot convert range: " + ex.getMessage()));
        }
    }
}
