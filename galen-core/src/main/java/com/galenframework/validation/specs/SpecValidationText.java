/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation.specs;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.galenframework.page.Rect;
import com.galenframework.specs.SpecText;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;

import java.util.List;
import java.util.regex.Pattern;

public class SpecValidationText<T extends SpecText> extends SpecValidation<T> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        Rect area = mainObject.getArea();
        String realText = mainObject.getText();
        if (realText == null) {
            realText = "";
        }

        realText = applyOperationsTo(realText, spec.getOperations());
        checkValue(spec, objectName, realText, "text", area);

        return new ValidationResult(spec, asList(new ValidationObject(area, objectName)));
    }

    private String applyOperationsTo(String text, List<String> operations) {
        if (operations != null) {
            for (String operation : operations) {
                text = TextOperation.find(operation).apply(text);
            }
        }
        return text;
    }

    protected void checkValue(SpecText spec, String objectName, String realText, String checkEntity, Rect area) throws ValidationErrorException {
        if (spec.getType() == SpecText.Type.IS) {
            checkIs(objectName, area, realText, spec.getText(), checkEntity);
        }
        if (spec.getType() == SpecText.Type.CONTAINS) {
            checkContains(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecText.Type.STARTS) {
            checkStarts(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecText.Type.ENDS) {
            checkEnds(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecText.Type.MATCHES) {
            checkMatches(objectName, area, realText, spec.getText(), checkEntity);
        }
    }


    protected void checkIs(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
        if (!realText.equals(text)) {
            throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should be \"%s\"", objectName, checkEntity, realText, text)));
        }
    }

    protected void checkStarts(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
        if (!realText.startsWith(text)) {
        	throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should start with \"%s\"", objectName, checkEntity, realText, text)));
        }
    }
    
    protected void checkEnds(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
        if (!realText.endsWith(text)) {
        	throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should end with \"%s\"", objectName, checkEntity, realText, text)));
        }
    }
    
    protected void checkMatches(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
        Pattern regex = Pattern.compile(text, Pattern.DOTALL);
        if (!regex.matcher(realText).matches()) {
        	throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should match \"%s\"", objectName, checkEntity, realText, text)));
        }
    }

    protected void checkContains(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
        if (!realText.contains(text)) {
        	throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should contain \"%s\"", objectName, checkEntity, realText, text)));
        }
    }
    

}
