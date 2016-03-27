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
package com.galenframework.rainbow4j;


import com.galenframework.rainbow4j.colorscheme.ColorClassifier;
import com.galenframework.rainbow4j.colorscheme.CustomSpectrum;
import com.galenframework.rainbow4j.filters.ImageFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Rainbow4J {

    public static final int DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM = 3;

    public static Spectrum readSpectrum(BufferedImage image) throws IOException {
        return readSpectrum(image, null, 256);
    }
    
    public static Spectrum readSpectrum(BufferedImage image, Rectangle rectangle) throws IOException {
        return readSpectrum(image, rectangle, 256);
    }
    
    public static Spectrum readSpectrum(BufferedImage image, int precision) throws IOException {
        return readSpectrum(image, null, precision);
    }



    public static ImageCompareResult compare(BufferedImage imageA, BufferedImage imageB, ComparisonOptions options) throws IOException {
        return compare(imageA, imageB,
                new Rectangle(0, 0, imageA.getWidth(), imageA.getHeight()),
                new Rectangle(0, 0, imageB.getWidth(), imageB.getHeight()),
                options);
    }


    public static ImageCompareResult compare(BufferedImage imageA, BufferedImage imageB, Rectangle areaA, Rectangle areaB, ComparisonOptions options) {
        if (options.getTolerance() < 0 ) {
            options.setTolerance(0);
        }

        if (areaA.width + areaA.x > imageA.getWidth() ||
            areaA.height + areaA.y > imageA.getHeight()) {
            throw new RuntimeException("Specified area is outside for original image");
        }
        if (areaB.width + areaB.x > imageB.getWidth() ||
                areaB.height + areaB.y > imageB.getHeight()) {
            throw new RuntimeException("Specified area is outside for secondary image");
        }

        int imageAWidth = imageA.getWidth();
        int imageAHeight = imageA.getHeight();

        int Cax = areaA.x;
        int Cay = areaA.y;

        int Cbx = areaB.x;
        int Cby = areaB.y;

        int Wa = areaA.width;
        int Ha = areaA.height;

        int Wb = areaB.width;
        int Hb = areaB.height;

        double Kx = ((double)Wb) / ((double)Wa);
        double Ky = ((double)Hb) / ((double)Ha);


        ImageHandler handlerA = new ImageHandler(imageA);
        ImageHandler handlerB = new ImageHandler(imageB);


        applyAllFilters(areaA, areaB, options, handlerA, handlerB);

        int tolerance = options.getTolerance();

        long minMismatchingPixels = Integer.MAX_VALUE;

        ImageHandler resultingMapHandler = null;


        int resultingOffsetX = 0;
        int resultingOffsetY = 0;


        // Here it moves within a spiral for better performance when analyzing a large offset
        int offsetX = 0;
        int offsetY = 0;
        int spiral_dx = 0;
        int spiral_dy = -1;

        int spiral_n = 0;

        if (options.getAnalyzeOffset() > 0) {
            spiral_n = options.getAnalyzeOffset() * 2 + 1;
        }
        int max_spiral = spiral_n * spiral_n;

        for (int spiral_i = 0; spiral_i <= max_spiral; spiral_i++) {

            if ((offsetX == offsetY) || (offsetX < 0 && offsetX == -offsetY) || (offsetX > 0 && offsetX == 1 - offsetY)){
                int temp = spiral_dx;
                spiral_dx = -spiral_dy;
                spiral_dy = temp;
            }

            ImageHandler mapHandler = new ImageHandler(areaA.width, areaA.height);
            long mismatchingPixels = 0;
            int x = 0, y = 0;

            while(y < Ha && minMismatchingPixels > 0) {
                while (x < Wa && mismatchingPixels < minMismatchingPixels) {
                    int xA = x + Cax + offsetX;
                    int yA = y + Cay + offsetY;

                    if (xA >= 0 && xA < imageAWidth && yA >= 0 && yA < imageAHeight) {

                        Color cA = handlerA.pickColor(xA, yA);

                        int xB, yB;

                        if (options.isStretchToFit()) {
                            xB = (int) Math.round((((double) x) * Kx) + Cbx);
                            yB = (int) Math.round(((double) y) * Ky + Cby);
                            xB = Math.min(xB, Cbx + Wb - 1);
                            yB = Math.min(yB, Cby + Hb - 1);
                        } else {
                            xB = x + Cbx;
                            yB = y + Cby;
                        }

                        Color cB = handlerB.pickColor(xB, yB);

                        long colorError = ImageHandler.colorDiff(cA, cB);
                        if (colorError > tolerance) {

                            Color color = Color.red;

                            int diff = (int) (colorError - tolerance);
                            if (diff > 30 && diff < 80) {
                                color = Color.yellow;
                            } else if (diff <= 30) {
                                color = Color.green;
                            }
                            mapHandler.setRGBA(x, y, color.getRed(), color.getGreen(), color.getBlue(), 255);

                            mismatchingPixels += 1;
                        } else {
                            mapHandler.setRGBA(x, y, 0, 0, 0, 255);
                        }
                    } else {
                        mapHandler.setRGBA(x, y, 0, 0, 0, 255);
                    }

                    x += 1;
                }
                y += 1;
                x = 0;
            }

            if (mismatchingPixels < minMismatchingPixels) {
                minMismatchingPixels = mismatchingPixels;
                resultingOffsetX = offsetX;
                resultingOffsetY = offsetY;
                resultingMapHandler = mapHandler;
            }

            offsetX += spiral_dx;
            offsetY += spiral_dy;
        }


        applyFilters(resultingMapHandler, options.getMapFilters(), new Rectangle(0, 0, resultingMapHandler.getWidth(), resultingMapHandler.getHeight()));

        ImageCompareResult result = analyzeComparisonMap(resultingMapHandler);
        result.setOffsetX(resultingOffsetX);
        result.setOffsetY(resultingOffsetY);

        result.setOriginalFilteredImage(handlerA.getImage().getSubimage(areaA.x, areaA.y, areaA.width, areaA.height));
        result.setSampleFilteredImage(handlerB.getImage().getSubimage(areaB.x, areaB.y, areaB.width, areaB.height));

        return result;
    }

    private static ImageCompareResult analyzeComparisonMap(ImageHandler mapHandler) {
        ImageCompareResult result = new ImageCompareResult();

        long totalMismatchingPixels = 0;

        ByteBuffer bytes = mapHandler.getBytes();

        for (int k = 0; k < bytes.capacity() - ImageHandler.BLOCK_SIZE; k += ImageHandler.BLOCK_SIZE) {
            if (((int)bytes.get(k) &0xff) > 0 || ((int)bytes.get(k + 1) &0xff) > 0 || ((int)bytes.get(k + 2) &0xff) > 0) {
                totalMismatchingPixels++;
            }
        }

        double totalPixels = (mapHandler.getWidth() * mapHandler.getHeight());
        result.setPercentage(100.0 * totalMismatchingPixels / totalPixels);
        result.setTotalPixels(totalMismatchingPixels);
        result.setComparisonMap(mapHandler.getImage());
        return result;
    }

    private static void applyAllFilters(Rectangle areaA, Rectangle areaB, ComparisonOptions options, ImageHandler handlerA, ImageHandler handlerB) {
        applyFilters(handlerA, options.getOriginalFilters(), areaA);
        applyFilters(handlerB, options.getSampleFilters(), areaB);
    }

    private static void applyFilters(ImageHandler handler, List<ImageFilter> filters, Rectangle area) {
        if (filters != null) {
            for (ImageFilter filter : filters) {
                handler.applyFilter(filter, area);
            }
        }
    }

    /**
     *
     * @param image an image for calculating the color spectrum
     * @param precision 8 to 256 value for spectrum accuracy. The bigger value - the better precision, but the more memory it takes
     * @return
     * @throws IOException
     */
    public static Spectrum readSpectrum(BufferedImage image, Rectangle area, int precision) throws IOException {

        if (precision < 8) throw new IllegalArgumentException("Color size should not be less then 8");
        if (precision > 256) throw new IllegalArgumentException("Color size should not be bigger then 256");

        int spectrum[][][] = new int[precision][precision][precision];

        int width = image.getWidth();
        int height = image.getHeight();

        int[] a = new int[width * height];

        image.getRGB(0, 0, width, height, a, 0, width);

        int spectrumWidth = width;
        int spectrumHeight = height;

        if (area == null) {
            area = new Rectangle(0, 0, width, height);
        }
        else {
            spectrumWidth = area.width;
            spectrumHeight = area.height;
        }

        int k = 0;
        int r,g,b;

        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                k = y * width + x;

                r = ((a[k] >> 16) & 0xff) * precision / 256;
                g = ((a[k] >> 8) & 0xff) * precision / 256;
                b = ((a[k]) & 0xff) * precision / 256;

                spectrum[Math.min(r, precision - 1)][Math.min(g, precision - 1)][Math.min(b, precision - 1)] += 1;
            }
        }

        return new Spectrum(spectrum, spectrumWidth, spectrumHeight);
    }

    public static CustomSpectrum readCustomSpectrum(BufferedImage image, List<ColorClassifier> colorClassifiers) {
        return readCustomSpectrum(image, colorClassifiers, new Rectangle(0, 0, image.getWidth(), image.getHeight()));
    }

    public static CustomSpectrum readCustomSpectrum(BufferedImage image, List<ColorClassifier> colorClassifiers, Rectangle area) {
        return readCustomSpectrum(image, colorClassifiers, area, DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM);
    }

    public static CustomSpectrum readCustomSpectrum(BufferedImage image, List<ColorClassifier> colorClassifiers, Rectangle area, int colorTolerance) {
        int maxColorSquareDistance = colorTolerance*colorTolerance*3;

        Map<String, AtomicInteger> colorPickers = new HashMap<>();
        for (ColorClassifier classifier : colorClassifiers) {
            colorPickers.put(classifier.getName(), new AtomicInteger(0));
        }

        int amountOfUnmatchedColor = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        int[] a = new int[width * height];

        image.getRGB(0, 0, width, height, a, 0, width);

        if (area == null) {
            area = new Rectangle(0, 0, width, height);
        }

        int k = 0;
        int r,g,b;

        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                k = y * width + x;

                r = ((a[k] >> 16) & 0xff);
                g = ((a[k] >> 8) & 0xff);
                b = ((a[k]) & 0xff);

                boolean colorMatched = false;
                for (ColorClassifier classifier : colorClassifiers) {
                    if (classifier.holdsColor(r, g, b, maxColorSquareDistance)) {
                        colorPickers.get(classifier.getName()).incrementAndGet();
                        colorMatched = true;
                    }
                }

                if (!colorMatched) {
                    amountOfUnmatchedColor += 1;
                }
            }
        }

        int totalPixels = area.height * area.width;
        Map<String, Integer> collectedColors = colorPickers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return new CustomSpectrum(collectedColors, amountOfUnmatchedColor, totalPixels);
    }

    public static BufferedImage loadImage(String filePath) throws IOException{
        return ImageIO.read(new File(filePath));
    }

    public static BufferedImage loadImage(InputStream stream) throws IOException {
        return ImageIO.read(stream);
    }

    public static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }

}
