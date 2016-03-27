/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
import java.util.stream.Collectors;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.colorscheme.ColorClassifier;
import com.galenframework.rainbow4j.colorscheme.CustomSpectrum;
import com.galenframework.rainbow4j.colorscheme.SimpleColorClassifier;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.SpecColorScheme;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;
import com.galenframework.rainbow4j.Rainbow4J;

import static java.util.Arrays.asList;

public class SpecValidationColorScheme extends SpecValidation<SpecColorScheme> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecColorScheme spec) throws ValidationErrorException {
        int colorTolerance = GalenConfig.getConfig().getIntProperty(GalenProperty.SPEC_COLORSCHEME_TOLERANCE, 0, 256);

        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        
        BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        
        Rect area = mainObject.getArea();
        if (pageImage.getWidth() < area.getLeft() + area.getWidth() || pageImage.getHeight() < area.getTop() + area.getHeight()) {
            throw new ValidationErrorException()
                .withValidationObject(new ValidationObject(area, objectName))
                .withMessage("Can't fetch image for \"object\" as it is outside of screenshot");
        }

        List<ColorClassifier> classifiers = spec.getColorRanges().stream().map(
                cr -> new SimpleColorClassifier(cr.getName(), cr.getColor())
        ).collect(Collectors.toList());
        

        CustomSpectrum spectrum;
        try {
            spectrum = Rainbow4J.readCustomSpectrum(pageImage, classifiers, new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight()), colorTolerance);
        } catch (Exception e) {
            throw new ValidationErrorException(String.format("Couldn't fetch spectrum for \"%s\"", objectName));
        }
        
        List<String> messages = new LinkedList<>();
        
        for (ColorRange colorRange : spec.getColorRanges()) {
            double realPercentage = 0;
            int totalPixels = spectrum.getTotalPixels();
            if (totalPixels > 0) {
                realPercentage = ((double)(spectrum.getCollectedColors().getOrDefault(colorRange.getName(), 0)) / totalPixels) * 100.0;
                if (realPercentage > 151) {
                    int j =0;
                }
            }

            if (!colorRange.getRange().holds(realPercentage)) {
                String realPercentageText = new RangeValue(realPercentage, colorRange.getRange().findPrecision()).toString();

                messages.add(String.format("color %s on \"%s\" is %s%% %s", colorRange.getName(), objectName, realPercentageText, colorRange.getRange().getErrorMessageSuffix("%")));
            }
        }


        List<ValidationObject> objects = asList(new ValidationObject(area, objectName));
        
        if (messages.size() > 0) {
            throw new ValidationErrorException()
                    .withValidationObjects(objects)
                    .withMessages(messages);
        }

        return new ValidationResult(spec, objects);
    }


}
