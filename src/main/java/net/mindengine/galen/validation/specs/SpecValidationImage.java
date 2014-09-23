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
import net.mindengine.galen.validation.*;
import net.mindengine.rainbow4j.ComparisonOptions;
import net.mindengine.rainbow4j.ImageCompareResult;
import net.mindengine.rainbow4j.Rainbow4J;

import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class SpecValidationImage extends SpecValidation<SpecImage> {

    private static class ImageCheck {

        private final String imagePath;
        private final double difference;
        private final ImageCompareResult result;
        private final String errorMessage;

        public ImageCheck(String imagePath, double difference, ImageCompareResult result, String errorMessage) {
            this.imagePath = imagePath;
            this.difference = difference;
            this.result = result;
            this.errorMessage = errorMessage;
        }
    }

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecImage spec) throws ValidationErrorException {
        PageElement pageElement = pageValidation.findPageElement(objectName);
        checkAvailability(pageElement, objectName);

        final BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        BufferedImage sampleImage;

        int tolerance = spec.getTolerance() != null ? Math.abs(spec.getTolerance()) : 25;

        ComparisonOptions options = new ComparisonOptions();
        options.setStretchToFit(spec.isStretch());
        options.setOriginalFilters(spec.getOriginalFilters());
        options.setSampleFilters(spec.getSampleFilters());
        options.setMapFilters(spec.getMapFilters());
        options.setTolerance(tolerance);


        Rect elementArea = pageElement.getArea();

        ImageCheck minCheck = new ImageCheck(spec.getImagePaths().get(0), elementArea.getHeight() * elementArea.getWidth() * 2, null, null);

        Iterator<String> it = spec.getImagePaths().iterator();

        if (!it.hasNext()) {
            throw new ValidationErrorException("There are now images defined to compare with")
                    .withErrorArea(new ErrorArea(pageElement.getArea(), objectName));
        }

        while (minCheck.difference > 0 && it.hasNext()) {
            String imagePath = it.next();

            ImageCheck imageCheck = checkImages(spec, pageImage, options, elementArea, imagePath);
            if (imageCheck.difference <= minCheck.difference) {
                minCheck = imageCheck;
            }
        }

        if (minCheck.difference > 0) {
            throw new ValidationErrorException(minCheck.errorMessage)
                    .withErrorArea(new ErrorArea(pageElement.getArea(), objectName))
                    .withImageComparison(new ImageComparison(spec.getSelectedArea(), minCheck.imagePath, minCheck.result.getComparisonMap()));
        }
    }

    private ImageCheck checkImages(SpecImage spec, BufferedImage pageImage, ComparisonOptions options, Rect elementArea, String imagePath) throws ValidationErrorException {
        BufferedImage sampleImage;
        try {
            InputStream stream = GalenUtils.findFileOrResourceAsStream(imagePath);
            sampleImage = Rainbow4J.loadImage(stream);
        } catch (Throwable e) {
            throw new ValidationErrorException("Couldn't load image: " + spec.getImagePaths().get(0));
        }

        Rectangle sampleArea = spec.getSelectedArea() != null ? toRectangle(spec.getSelectedArea()) : new Rectangle(0, 0, sampleImage.getWidth(), sampleImage.getHeight());
        ImageCompareResult result = Rainbow4J.compare(pageImage, sampleImage, toRectangle(elementArea), sampleArea, options);

        double difference = 0.0;
        String errorMessage = null;
        if (spec.getMaxPercentage() != null) {
            difference = result.getPercentage() - spec.getMaxPercentage();
            if (difference > 0) {
                errorMessage = createErrorMessageForPercentage(msgErrorPrefix(spec.getImagePaths().get(0)), spec.getMaxPercentage(), result.getPercentage());
            }
        } else {
            if (spec.getMaxPixels() == null) {
                spec.setMaxPixels(0);
            }

            difference = result.getTotalPixels() - spec.getMaxPixels();
            if (difference > 0) {
                errorMessage = createErrorMessageForPixels(msgErrorPrefix(spec.getImagePaths().get(0)), spec.getMaxPixels(), result.getTotalPixels());
            }
        }

        return new ImageCheck(imagePath, difference, result, errorMessage);
    }

    private String msgErrorPrefix(String imagePath) {
        return String.format("Element does not look like \"%s\". ", imagePath);
    }

    private String createErrorMessageForPixels(String msgPrefix, Integer maxPixels, long totalPixels) throws ValidationErrorException {
        return String.format("%sThere are %d mismatching pixels but max allowed is %d", msgPrefix, totalPixels, maxPixels);
    }

    private String createErrorMessageForPercentage(String msgPrefix, Double maxPercentage, double percentage) throws ValidationErrorException {
        return String.format("%sThere are %s%% mismatching pixels but max allowed is %s%%", msgPrefix, percentage, maxPercentage);
    }


    private Rectangle toRectangle(Rect area) {
        return new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight());
    }
}
