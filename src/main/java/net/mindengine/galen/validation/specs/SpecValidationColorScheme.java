/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import org.apache.commons.collections.CollectionUtils;

import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecColorScheme;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.galen.validation.*;
import net.mindengine.rainbow4j.Rainbow4J;
import net.mindengine.rainbow4j.Spectrum;
import static java.util.Arrays.asList;

public class SpecValidationColorScheme extends SpecValidation<SpecColorScheme> {

    private static final int PRECISION = GalenConfig.getConfig().getIntProperty("spec.colorscheme.precision", 256, 8, 256);
    private static final int TEST_RANGE = GalenConfig.getConfig().getIntProperty("spec.colorscheme.testrange", 6, 0, 256);

    @Override
    public ValidationResult check(final PageValidation pageValidation, final String objectName, final SpecColorScheme spec) throws ValidationErrorException {
        final PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        final BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();

        final Rect area = mainObject.getArea();
        if (pageImage.getWidth() < area.getLeft() + area.getWidth() || pageImage.getHeight() < area.getTop() + area.getHeight()) {
            throw new ValidationErrorException().withValidationObject(new ValidationObject(area, objectName)).withMessage(
                    "Can't fetch image for \"object\" as it is outside of screenshot");
        }

        Spectrum spectrum;
        try {
            spectrum = Rainbow4J.readSpectrum(pageImage, new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight()), PRECISION);
        } catch (final Exception e) {
            throw new ValidationErrorException(String.format("Couldn't fetch spectrum for \"%s\"", objectName));
        }

        final List<String> messages = new LinkedList<String>();

        for (final ColorRange colorRange : spec.getColorRanges()) {
            final Color color = colorRange.getColor();
            final int percentage = (int) spectrum.getPercentage(color.getRed(), color.getGreen(), color.getBlue(), TEST_RANGE);

            if (!colorRange.getRange().holds(percentage)) {
                messages.add(String.format("color %s on \"%s\" is %d%% %s", toHexColor(color), objectName, percentage, colorRange.getRange()
                        .getErrorMessageSuffix("%")));
            }
        }

        final List<ValidationObject> objects = asList(new ValidationObject(area, objectName));

        if (CollectionUtils.isNotEmpty(messages)) {
            throw new ValidationErrorException().withValidationObjects(objects).withMessages(messages);
        } else {
            return new ValidationResult(objects);
        }
    }

    private String toHexColor(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
