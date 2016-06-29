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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.page.Rect;
import com.galenframework.specs.SpecImage;
import com.galenframework.validation.*;
import com.galenframework.config.GalenConfig;
import com.galenframework.page.PageElement;
import com.galenframework.utils.GalenUtils;
import com.galenframework.rainbow4j.ComparisonOptions;
import com.galenframework.rainbow4j.ImageCompareResult;
import com.galenframework.rainbow4j.Rainbow4J;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

public class SpecValidationImage extends SpecValidation<SpecImage> {

    private final static Logger LOG = LoggerFactory.getLogger(SpecValidationImage.class);
    private static final String NO_ERROR_MESSAGE = null;
    private static final ImageCompareResult NO_RESULT = null;

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
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecImage spec) throws ValidationErrorException {
        PageElement pageElement = pageValidation.findPageElement(objectName);
        checkAvailability(pageElement, objectName);

        final BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();

        int tolerance = GalenConfig.getConfig().getImageSpecDefaultTolerance();

        if (spec.getTolerance() != null && spec.getTolerance() >= 0) {
            tolerance = spec.getTolerance();
        }

        ComparisonOptions options = new ComparisonOptions();
        options.setStretchToFit(spec.isStretch());
        options.setOriginalFilters(spec.getOriginalFilters());
        options.setSampleFilters(spec.getSampleFilters());
        options.setMapFilters(spec.getMapFilters());
        options.setTolerance(tolerance);
        options.setAnalyzeOffset(spec.getAnalyzeOffset());

        Rect elementArea = pageElement.getArea();

        List<String> realPaths = new LinkedList<>();
        for (String imagePossiblePath : spec.getImagePaths()) {
            if (imagePossiblePath.contains("*") || imagePossiblePath.contains("#")) {
                realPaths.addAll(GalenUtils.findFilesOrResourcesMatchingSearchExpression(imagePossiblePath));
            } else {
                realPaths.add(imagePossiblePath);
            }
        }

        if (realPaths.isEmpty()) {
            throw new ValidationErrorException("There are no images found").withValidationObject(new ValidationObject(pageElement.getArea(), objectName));
        }

        int largestPossibleDifference = elementArea.getHeight() * elementArea.getWidth() * 2;

        ImageCheck minCheck = new ImageCheck(realPaths.get(0), largestPossibleDifference, NO_RESULT, NO_ERROR_MESSAGE);

        Iterator<String> it = realPaths.iterator();

        try {
            while (minCheck.difference > 0 && it.hasNext()) {
                String imagePath = it.next();

                ImageCheck imageCheck = checkImages(spec, pageImage, options, elementArea, imagePath);
                if (imageCheck.difference <= minCheck.difference) {
                    minCheck = imageCheck;
                }
            }
        } catch (ValidationErrorException ex) {
            LOG.trace("Validation errors during image compare.", ex);
            ex.withValidationObject(new ValidationObject(pageElement.getArea(), objectName));
            throw ex;
        } catch (Exception ex) {
            LOG.trace("Unknown errors during image compare", ex);
            throw new ValidationErrorException(ex).withValidationObject(new ValidationObject(pageElement.getArea(), objectName));
        }

        List<ValidationObject> objects = asList(new ValidationObject(pageElement.getArea(), objectName));

        if (minCheck.difference > 0) {
            throw new ValidationErrorException(minCheck.errorMessage)
                    .withValidationObjects(objects)
                    .withImageComparison(new ImageComparison(
                            minCheck.result.getOriginalFilteredImage(),
                            minCheck.result.getSampleFilteredImage(),
                            minCheck.result.getComparisonMap()));
        }

        return new ValidationResult(spec, objects);
    }



    private ImageCheck checkImages(SpecImage spec, BufferedImage pageImage, ComparisonOptions options, Rect elementArea, String imagePath)
            throws ValidationErrorException {
        BufferedImage sampleImage;
        try {
            InputStream stream = GalenUtils.findFileOrResourceAsStream(imagePath);
            sampleImage = Rainbow4J.loadImage(stream);
        } catch (Exception ex) {
            LOG.error("Unknown errors during image check.", ex);
            throw new ValidationErrorException("Couldn't load image: " + spec.getImagePaths().get(0));
        }

        Rectangle sampleArea = spec.getSelectedArea() != null ? toRectangle(spec.getSelectedArea()) : new Rectangle(0, 0, sampleImage.getWidth(),
                sampleImage.getHeight());

        if (elementArea.getLeft() >= pageImage.getWidth() || elementArea.getTop() >= pageImage.getHeight()) {
            throw new RuntimeException(String.format(
                    "The page element is located outside of the screenshot. (Element {x: %d, y: %d, w: %d, h: %d}, Screenshot {w: %d, h: %d})", elementArea.getLeft(),
                    elementArea.getTop(), elementArea.getWidth(), elementArea.getHeight(), pageImage.getWidth(), pageImage.getHeight()));
        }

        if (spec.isCropIfOutside() || isOnlyOnePixelOutsideScreenshot(elementArea, pageImage)) {
            elementArea = cropElementAreaIfOutside(elementArea, pageImage.getWidth(), pageImage.getHeight());
        }

        ImageCompareResult result = Rainbow4J.compare(pageImage, sampleImage, toRectangle(elementArea), sampleArea, options);

        double difference = 0.0;
        String errorMessage = null;

        SpecImage.ErrorRate errorRate = spec.getErrorRate();
        if (errorRate == null) {
            errorRate = GalenConfig.getConfig().getImageSpecDefaultErrorRate();
        }

        if (errorRate.getType() == SpecImage.ErrorRateType.PERCENT) {
            difference = result.getPercentage() - errorRate.getValue();
            if (difference > 0) {
                errorMessage = createErrorMessageForPercentage(msgErrorPrefix(spec.getImagePaths().get(0)), errorRate.getValue(), result.getPercentage());
            }
        } else {
            difference = result.getTotalPixels() - errorRate.getValue();
            if (difference > 0) {
                errorMessage = createErrorMessageForPixels(msgErrorPrefix(spec.getImagePaths().get(0)), errorRate.getValue().intValue(), result.getTotalPixels());
            }
        }

        return new ImageCheck(imagePath, difference, result, errorMessage);
    }

    private boolean isOnlyOnePixelOutsideScreenshot(Rect elementArea, BufferedImage pageImage) {
        int dx = elementArea.getLeft() + elementArea.getWidth() - pageImage.getWidth();
        int dy = elementArea.getTop() + elementArea.getHeight() - pageImage.getHeight();

        return Math.max(dx, dy) == 1;
    }

    private Rect cropElementAreaIfOutside(Rect elementArea, int width, int height) {
        int x2 = elementArea.getLeft() + elementArea.getWidth();
        int y2 = elementArea.getTop() + elementArea.getHeight();

        int originalWidth = elementArea.getWidth();
        int originalHeight = elementArea.getHeight();

        if (originalWidth > 0 && originalHeight > 0) {
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (x2 >= width) {
                newWidth -= x2 - width + 1;
            }
            if (y2 >= height) {
                newHeight -= y2 - height + 1;
            }

            if ((double) (newWidth * newHeight) / (double) (originalWidth * originalHeight) < 0.5) {
                throw new RuntimeException(String.format(
                        "The cropped area is less than a half of element area (Element {x: %d, y: %d, w: %d, h: %d}, Screenshot {w: %d, h: %d})", elementArea.getLeft(),
                        elementArea.getTop(), newWidth, newHeight, width, height));
            }

            return new Rect(elementArea.getLeft(), elementArea.getTop(), newWidth, newHeight);
        }
        return elementArea;
    }

    private String msgErrorPrefix(String imagePath) {
        return String.format("Element does not look like \"%s\". ", imagePath);
    }

    private String createErrorMessageForPixels(String msgPrefix, Integer maxPixels, long totalPixels) throws ValidationErrorException {
        return String.format("%sThere are %d mismatching pixels but max allowed is %d", msgPrefix, totalPixels, maxPixels);
    }

    private String createErrorMessageForPercentage(String msgPrefix, Double maxPercentage, double percentage) throws ValidationErrorException {
        return String.format("%sThere are %s%% mismatching pixels but max allowed is %s%%", msgPrefix, formatDouble(percentage), formatDouble(maxPercentage));
    }

    private static final DecimalFormat _doubleFormat = new DecimalFormat("#.##");
    private String formatDouble(Double value) {
        return _doubleFormat.format(value);
    }

    private Rectangle toRectangle(Rect area) {
        return new Rectangle(area.getLeft(), area.getTop(), area.getWidth(), area.getHeight());
    }
}
