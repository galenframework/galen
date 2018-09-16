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
package com.galenframework.validation.specs;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import com.galenframework.ocr.GoogleVisionOcrService;
import com.galenframework.ocr.OcrResult;
import com.galenframework.ocr.OcrService;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.SpecOcr;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.SpecValidation;
import com.galenframework.validation.ValidationErrorException;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationResult;

public class SpecValidationOcr extends SpecValidation<SpecOcr> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecOcr spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        
        checkAvailability(mainObject, objectName);
        
        Rect area = mainObject.getArea();
        String domText = mainObject.getText();
        BufferedImage img = pageValidation.getBrowser().getPage().getScreenshotImage();

        OcrService ocrService = new GoogleVisionOcrService();
        OcrResult ocrResult = ocrService.findOcrText(img, area);
        if (ocrResult.getText() == null) {
            ocrResult.setText("");
        }
        ocrResult.setText(new String(ocrResult.getText().getBytes(Charset.forName("utf-8"))));

        String realText = applyOperationsTo(ocrResult.getText(), spec.getOperations());
        checkValue(spec, objectName, realText, "text", ocrResult.getRect(), domText);

        return new ValidationResult(spec, asList(new ValidationObject(ocrResult.getRect(), objectName)));
    }

    private String applyOperationsTo(String text, List<String> operations) {
        if (operations != null) {
            for (String operation : operations) {
                text = TextOperation.find(operation).apply(text);
            }
        }
        return text;
    }

    protected void checkValue(SpecOcr spec, String objectName, String realText, String checkEntity, Rect area, String documentText) throws ValidationErrorException {
        if (spec.getType() == SpecOcr.Type.IS) {
            checkIs(objectName, area, realText, spec.getText(), checkEntity);
        }
        if (spec.getType() == SpecOcr.Type.CONTAINS) {
            checkContains(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecOcr.Type.STARTS) {
            checkStarts(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecOcr.Type.ENDS) {
            checkEnds(objectName, area, realText, spec.getText(), checkEntity);
        }
        else if (spec.getType() == SpecOcr.Type.MATCHES) {
            checkMatches(objectName, area, realText, spec.getText(), checkEntity);
        }else if (spec.getType() == SpecOcr.Type.DOMIS) {
        	checkIs(objectName, area, realText, documentText, checkEntity);
        }else if (spec.getType() == SpecOcr.Type.DOM_STARTS) {
        	checkStarts(objectName, area, realText, documentText, checkEntity);
        }
    }


    protected void checkIs(String objectName, Rect area, String realText, String text, String checkEntity) throws ValidationErrorException {
    	if(realText.equals(text) || realText.replaceAll("\\w+", "").equals(text.replaceAll("\\w+", ""))) {
    		return;
    	}
        throw new ValidationErrorException(asList(new ValidationObject(area, objectName)), asList(format("\"%s\" %s is \"%s\" but should be \"%s\"", objectName, checkEntity, realText, text)));
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
