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
package net.mindengine.galen.validation.specs;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecText;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;

public class SpecValidationText extends SpecValidation<SpecText> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecText spec) throws ValidationErrorException {
        
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        Rect area = mainObject.getArea();
        String realText = mainObject.getText();
        if (realText == null) {
            realText = "";
        }
        
        
        if (spec.getType() == SpecText.Type.IS) {
            checkIs(objectName, area, realText, spec.getText());
        }
        if (spec.getType() == SpecText.Type.CONTAINS) {
            checkContains(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.STARTS) {
            checkStarts(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.ENDS) {
            checkEnds(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.MATCHES) {
            checkMatches(objectName, area, realText, spec.getText());
        }
    }

    private void checkStarts(String objectName, Rect area, String realText, String text) throws ValidationErrorException {
        if (!realText.startsWith(text)) {
        	throw new ValidationErrorException(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should start with \"%s\"", objectName, realText, text)));
        }
    }
    
    private void checkEnds(String objectName, Rect area, String realText, String text) throws ValidationErrorException {
        if (!realText.endsWith(text)) {
        	throw new ValidationErrorException(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should end with \"%s\"", objectName, realText, text)));
        }
    }
    
    private void checkMatches(String objectName, Rect area, String realText, String text) throws ValidationErrorException {
        if (!realText.matches(text)) {
        	throw new ValidationErrorException(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should match \"%s\"", objectName, realText, text)));
        }
    }

    private void checkContains(String objectName, Rect area, String realText, String text) throws ValidationErrorException {
        if (!realText.contains(text)) {
        	throw new ValidationErrorException(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should contain \"%s\"", objectName, realText, text)));
        }
    }
    
    private void checkIs(String objectName, Rect area, String realText, String text) throws ValidationErrorException {
        if (!realText.equals(text)) {
        	throw new ValidationErrorException(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should be \"%s\"", objectName, realText, text)));
        }
    }

}
