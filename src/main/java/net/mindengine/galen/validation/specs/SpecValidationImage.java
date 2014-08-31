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

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecImage;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;
import net.mindengine.rainbow4j.ImageCompareResult;
import net.mindengine.rainbow4j.Rainbow4J;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

public class SpecValidationImage extends SpecValidation<SpecImage> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecImage spec) throws ValidationErrorException {
        PageElement pageElement = pageValidation.findPageElement(objectName);
        checkAvailability(pageElement, objectName);

        final BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        final BufferedImage sampleImage;
        try {
            InputStream stream = GalenUtils.findFileOrResourceAsStream(spec.getImagePath());
            sampleImage = Rainbow4J.loadImage(stream);
        } catch (Throwable e) {
            throw new ValidationErrorException("Couldn't load image: " + spec.getImagePath());
        }


        int smooth = spec.getSmooth() != null ? Math.abs(spec.getSmooth()) : 0;
        int tolerance = spec.getTolerance() != null ? Math.abs(spec.getTolerance()) : 25;

        Rectangle sampleArea = spec.getSelectedArea() != null ? toRectangle(spec.getSelectedArea()) : new Rectangle(0, 0, sampleImage.getWidth(), sampleImage.getHeight());
        ImageCompareResult result = Rainbow4J.compare(pageImage, sampleImage, smooth, tolerance, toRectangle(pageElement.getArea()), sampleArea);

        try {
            if (spec.getMaxPercentage() != null) {
                compareResultByPercentage(msgErrorPrefix(spec.getImagePath()), spec.getMaxPercentage(), result.getPercentage());
            } else if (spec.getMaxPixels() != null) {
                compareResultByPixels(msgErrorPrefix(spec.getImagePath()), spec.getMaxPixels(), result.getTotalPixels());
            } else
                throw new ValidationErrorException("Can't verify this spec as neither max pixels or percentage is defined");
        }
        catch (ValidationErrorException validationErrorException) {
            validationErrorException.setErrorAreas(new LinkedList<ErrorArea>());
            validationErrorException.getErrorAreas().add(new ErrorArea(pageElement.getArea(), objectName));
            throw validationErrorException;
        }
    }

    private String msgErrorPrefix(String imagePath) {
        return String.format("Element does not look like \"%s\". ", imagePath);
    }

    private void compareResultByPixels(String msgPrefix, Integer maxPixels, long totalPixels) throws ValidationErrorException {
        if (totalPixels > maxPixels) {
            throw new ValidationErrorException(String.format("%sThere are %d mismatching pixels but max allowed is %d", msgPrefix, totalPixels, maxPixels));
        }
    }

    private void compareResultByPercentage(String msgPrefix, Double maxPercentage, double percentage) throws ValidationErrorException {
        if (percentage > maxPercentage) {
            throw new ValidationErrorException(String.format("%sThere are %s%% mismatching pixels but max allowed is %s%%", msgPrefix, percentage, maxPercentage));
        }
    }


    private Rectangle toRectangle(Rect area) {
        return new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight());
    }
}
