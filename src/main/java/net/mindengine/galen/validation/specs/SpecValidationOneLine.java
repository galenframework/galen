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
package net.mindengine.galen.validation.specs;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecObjectsOnOneLine;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public abstract class SpecValidationOneLine<T extends SpecObjectsOnOneLine> extends SpecValidation<T> {

    @Override
    public void check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = getPageElement(pageValidation, objectName);
        
        checkAvailability(mainObject, objectName);
        
        List<String> misalignedObjectNames = new LinkedList<String>();
        List<ErrorArea> errorAreas = new LinkedList<ErrorArea>();
        
        for (String childObjectName : fetchChildObjets(spec.getChildObjects(), pageValidation.getPageSpec())) {
            PageElement childObject = getPageElement(pageValidation, childObjectName);
            checkAvailability(childObject, childObjectName);
            
            if (Math.abs(getOffset(spec, mainObject, childObject)) > 1) {
                misalignedObjectNames.add(childObjectName);
                errorAreas.add(new ErrorArea(childObject.getArea(), childObjectName));
            }
        }
        
        if (misalignedObjectNames.size() > 0) {
        	throw new ValidationErrorException(errorAreas, Arrays.asList(errorMisalignedObjects(objectName, misalignedObjectNames, spec)));
        }
    }

    private String errorMisalignedObjects(String objectName, List<String> misalignedObjectNames, T spec) {
        String pattern = null;
        if (misalignedObjectNames.size() > 1) {
            pattern = "%s are not aligned %s with \"%s\"";
        }
        else {
            pattern = "%s is not aligned %s with \"%s\"";
        }
        return (String.format(pattern, convertObjectNameToCommaSeparated(misalignedObjectNames), getAligmentText(spec), objectName));
    }

    private String convertObjectNameToCommaSeparated(List<String> misalignedObjectNames) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (String objectName : misalignedObjectNames) {
            if (!first) {
                buffer.append(", ");
            }
            else first = false;
            buffer.append("\"");
            buffer.append(objectName);
            buffer.append("\"");
        }
        return buffer.toString();
    }

    protected abstract String getAligmentText(T spec);

    protected abstract int getOffset(T spec, PageElement mainObject, PageElement childObject);

}
