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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecText;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;

public class SpecValidationText extends SpecValidation<SpecText> {

    @Override
    public ValidationError check(PageValidation pageValidation, String objectName, SpecText spec) {
        
        PageElement mainObject = getPageElement(pageValidation, objectName);
        
        ValidationError error = checkAvailability(mainObject, objectName);
        if (error != null) {
            return error;
        }
        
        Rect area = mainObject.getArea();
        String realText = mainObject.getText();
        if (realText == null) {
            realText = "";
        }
        
        
        if (spec.getType() == SpecText.Type.IS) {
            return checkIs(objectName, area, realText, spec.getText());
        }
        if (spec.getType() == SpecText.Type.CONTAINS) {
            return checkContains(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.STARTS) {
            return checkStarts(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.ENDS) {
            return checkEnds(objectName, area, realText, spec.getText());
        }
        else if (spec.getType() == SpecText.Type.MATCHES) {
            return checkMatches(objectName, area, realText, spec.getText());
        }
        return null;
    }

    private ValidationError checkStarts(String objectName, Rect area, String realText, String text) {
        if (!realText.startsWith(text)) {
            return new ValidationError(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should start with \"%s\"", objectName, realText, text)));
        }
        else return null;
    }
    
    private ValidationError checkEnds(String objectName, Rect area, String realText, String text) {
        if (!realText.endsWith(text)) {
            return new ValidationError(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should end with \"%s\"", objectName, realText, text)));
        }
        else return null;
    }
    
    private ValidationError checkMatches(String objectName, Rect area, String realText, String text) {
        if (!realText.matches(text)) {
            return new ValidationError(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should match \"%s\"", objectName, realText, text)));
        }
        else return null;
    }

    private ValidationError checkContains(String objectName, Rect area, String realText, String text) {
        if (!realText.contains(text)) {
            return new ValidationError(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should contain \"%s\"", objectName, realText, text)));
        }
        else return null;
    }
    
    private ValidationError checkIs(String objectName, Rect area, String realText, String text) {
        if (!realText.equals(text)) {
            return new ValidationError(asList(new ErrorArea(area, objectName)), asList(format("\"%s\" text is \"%s\" but should be \"%s\"", objectName, realText, text)));
        }
        else return null;
    }

}
