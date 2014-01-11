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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecColorScheme;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;
import net.mindengine.rainbow4j.Rainbow4J;
import net.mindengine.rainbow4j.Spectrum;

public class SpecValidationColorScheme extends SpecValidation<SpecColorScheme> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecColorScheme spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        
        BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        
        Rect area = mainObject.getArea();
        if (pageImage.getWidth() < area.getLeft() + area.getWidth() || pageImage.getHeight() < area.getTop() + area.getHeight()) {
            throw new ValidationErrorException()
                .withErrorArea(new ErrorArea(area, objectName))
                .withMessage("Can't fetch image for \"object\" as it is outside of screenshot");
        }
        
        Spectrum spectrum;
        try {
            spectrum = Rainbow4J.readSpectrum(pageImage, new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight()), 256);
        } catch (Exception e) {
            throw new ValidationErrorException(String.format("Couldn't fetch spectrum for \"%s\"", objectName));
        }
        
        List<String> messages = new LinkedList<String>();
        
        for (ColorRange colorRange : spec.getColorRanges()) {
            Color color = colorRange.getColor();
            int percentage = (int)spectrum.getPercentage(color.getRed(), color.getGreen(), color.getBlue(), 6);
            
            if (!colorRange.getRange().holds(percentage)) {
                messages.add(String.format("color %s on \"%s\" is %d%% %s", toHexColor(color), objectName, (int)percentage, colorRange.getRange().getErrorMessageSuffix("%")));
            }
        }
        
        if (messages.size() > 0) {
            throw new ValidationErrorException()
                    .withErrorArea(new ErrorArea(area, objectName))
                    .withMessages(messages);
        }
    }

    private String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
