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
package net.mindengine.rainbow4j;


import net.mindengine.rainbow4j.filters.ImageFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class Rainbow4J {

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

        ImageHandler mapHandler = new ImageHandler(areaA.width, areaA.height);

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

        int x = 0, y = 0;

        applyAllFilters(areaA, areaB, options, handlerA, handlerB);

        int tolerance = options.getTolerance();

        while(y < Ha) {
            while (x < Wa) {
                int xA = x + Cax;
                int yA = y + Cay;

                Color cA = handlerA.pickColor(xA, yA);

                int xB, yB;

                if (options.isStretchToFit()) {
                    xB = (int) Math.round((((double) x) * Kx) + Cbx);
                    yB = (int) Math.round(((double) y) * Ky + Cby);
                    xB = Math.min(xB, Cbx + Wb - 1);
                    yB = Math.min(yB, Cby + Hb - 1);
                }
                else {
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
                    }
                    else if (diff <= 30){
                        color = Color.green;
                    }
                    mapHandler.setRGB(x, y, color.getRed(), color.getGreen(), color.getBlue());
                }
                else {
                    mapHandler.setRGB(x, y, 0, 0, 0);
                }

                x += 1;
            }
            y += 1;
            x = 0;
        }

        applyFilters(mapHandler, options.getMapFilters(), new Rectangle(0, 0, mapHandler.getWidth(), mapHandler.getHeight()));

        ImageCompareResult result = analyzeComparisonMap(mapHandler);

        result.setOriginalFilteredImage(handlerA.getImage().getSubimage(areaA.x, areaA.y, areaA.width, areaA.height));
        result.setSampleFilteredImage(handlerB.getImage().getSubimage(areaB.x, areaB.y, areaB.width, areaB.height));

        return result;
    }

    private static ImageCompareResult analyzeComparisonMap(ImageHandler mapHandler) {
        ImageCompareResult result = new ImageCompareResult();

        int totalMismatchingPixels = 0;

        byte[] bytes = mapHandler.getBytes();

        for (int k = 0; k < bytes.length - 3; k += 3) {
            if (((int)bytes[k] &0xff) > 0 || ((int)bytes[k + 1] &0xff) > 0 || ((int)bytes[k + 2] &0xff) > 0) {
                totalMismatchingPixels++;
            }
        }

        double totalPixels = (mapHandler.getWidth() * mapHandler.getHeight());
        result.setPercentage(100.0 * totalMismatchingPixels / totalPixels);
        result.setTotalPixels((long)totalMismatchingPixels);
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
