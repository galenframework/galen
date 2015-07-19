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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.Rect;
import com.galenframework.specs.SpecColorScheme;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;
import com.galenframework.rainbow4j.Rainbow4J;
import com.galenframework.rainbow4j.Spectrum;

import static java.util.Arrays.asList;

public class SpecValidationColorScheme extends SpecValidation<SpecColorScheme> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecColorScheme spec) throws ValidationErrorException {
        int colorPrecision = GalenConfig.getConfig().getIntProperty(GalenProperty.SPEC_COLORSCHEME_PRECISON, 8, 256);
        int colorTestRange = GalenConfig.getConfig().getIntProperty(GalenProperty.SPEC_COLORSCHEME_TESTRANGE, 0, 256);

        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        
        BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        
        Rect area = mainObject.getArea();
        if (pageImage.getWidth() < area.getLeft() + area.getWidth() || pageImage.getHeight() < area.getTop() + area.getHeight()) {
            throw new ValidationErrorException()
                .withValidationObject(new ValidationObject(area, objectName))
                .withMessage("Can't fetch image for \"object\" as it is outside of screenshot");
        }
        
        
        
        
        
        Spectrum spectrum;
        try {
            spectrum = Rainbow4J.readSpectrum(pageImage, new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight()), colorPrecision);
        } catch (Exception e) {
            throw new ValidationErrorException(String.format("Couldn't fetch spectrum for \"%s\"", objectName));
        }
        
        List<String> messages = new LinkedList<String>();
        
        for (ColorRange colorRange : spec.getColorRanges()) {
            Color color = colorRange.getColor();
            int percentage = (int)spectrum.getPercentage(color.getRed(), color.getGreen(), color.getBlue(), colorTestRange);
            
            if (!colorRange.getRange().holds(percentage)) {
                messages.add(String.format("color %s on \"%s\" is %d%% %s", toHexColor(color), objectName, (int)percentage, colorRange.getRange().getErrorMessageSuffix("%")));
            }
        }


        List<ValidationObject> objects = asList(new ValidationObject(area, objectName));
        
        if (messages.size() > 0) {
            throw new ValidationErrorException()
                    .withValidationObjects(objects)
                    .withMessages(messages);
        }

        return new ValidationResult(objects);
    }

    private String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
