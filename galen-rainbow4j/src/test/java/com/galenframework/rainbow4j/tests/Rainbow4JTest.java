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
package com.galenframework.rainbow4j.tests;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.galenframework.rainbow4j.*;
import com.galenframework.rainbow4j.colorscheme.*;
import com.galenframework.rainbow4j.filters.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Rainbow4JTest {


    @Test
    public void shouldSave_imageAsPng() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/test-spectrum-black-white-1.png").getFile());

        File file = File.createTempFile("test-rainbow4j-image", ".png");
        Rainbow4J.saveImage(image, file);

        assertThat("File should exist", file.exists());
        BufferedImage image2 = Rainbow4J.loadImage(file.getAbsolutePath());
        assertThat("Width should be same as original width", image2.getWidth(), is(image.getWidth()));
        assertThat("Height should be same as original height", image2.getHeight(), is(image.getHeight()));
    }



    @Test
    public void shouldRead_imageSpectrum_withCustomPrecision() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/test-spectrum-black-white-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image, 64);
        
        Assert.assertEquals(spectrum.getPrecision(), 64);

        Assert.assertEquals((int)spectrum.getPercentage(255,255,255, 0), 68);
        Assert.assertEquals((int) spectrum.getPercentage(0, 0, 0, 0), 31);
        Assert.assertEquals((int) spectrum.getPercentage(128, 128, 128, 0), 0);

        Assert.assertEquals((int) spectrum.getPercentage(254, 254, 254, 0), 68);
        Assert.assertEquals((int) spectrum.getPercentage(253, 253, 253, 0), 68);
        Assert.assertEquals((int) spectrum.getPercentage(254, 254, 254, 5), 68);
        Assert.assertEquals((int) spectrum.getPercentage(254, 250, 254, 10), 68);
    }

    @Test
    public void shouldRead_imageSpectrum_withPredefinedColorClassifiers() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/colo-scheme-gradient.png").getFile());

        List<ColorClassifier> colorClassifiers = asList(
                new GradientColorClassifier("green-blue", asList(new Color(5, 153, 0), new Color(9, 24, 184))),
                new SimpleColorClassifier("pink", new Color(252, 18, 53)),
                new SimpleColorClassifier("blue", new Color(21, 2, 230))
        );
        CustomSpectrum spectrum = Rainbow4J.readCustomSpectrum(image, colorClassifiers);

        assertThat(spectrum.getCollectedColors(), is(new HashMap<String, Integer>() {{
            put("green-blue", 10000);
            put("pink", 10000);
            put("blue", 10000);
        }}));

        assertThat(spectrum.getOtherColors(), is(10000));
        assertThat(spectrum.getTotalPixels(), is(40000));
    }

    @Test
    public void shouldRead_imageSpectrum_withPredefinedColorClassifiers_inSpecifiedArea() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/colo-scheme-gradient.png").getFile());

        List<ColorClassifier> colorClassifiers = asList(
                new GradientColorClassifier("green-blue", asList(new Color(5, 153, 0), new Color(9, 24, 184)))
        );
        CustomSpectrum spectrum = Rainbow4J.readCustomSpectrum(image, colorClassifiers, new Rectangle(0, 0, 100, 100));


        assertThat(spectrum.getCollectedColors(), is(new HashMap<String, Integer>(){{
            put("green-blue", 10000);
        }}));
        assertThat(spectrum.getOtherColors(), is(0));
        assertThat(spectrum.getTotalPixels(), is(10000));
    }

    @Test
    public void shouldRead_imageSpectrum_withPredefinedColorClassifiers_inSpecifiedArea_2() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/colo-scheme-gradient.png").getFile());

        List<ColorClassifier> colorClassifiers = asList(
                new GradientColorClassifier("green-blue", asList(new Color(5, 153, 0), new Color(9, 24, 184)))
        );
        CustomSpectrum spectrum = Rainbow4J.readCustomSpectrum(image, colorClassifiers, new Rectangle(0, 0, 100, 100));


        assertThat(spectrum.getCollectedColors(), is(new HashMap<String, Integer>(){{
            put("green-blue", 10000);
        }}));
        assertThat(spectrum.getOtherColors(), is(0));
        assertThat(spectrum.getTotalPixels(), is(10000));
    }

    @Test
    public void shouldRead_imageSpectrum_withPredefinedColorClassifiers_inSpecifiedArea_3() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/colo-scheme-gradient.png").getFile());

        List<ColorClassifier> colorClassifiers = asList(
                new GradientColorClassifier("green-blue", asList(new Color(6, 120, 46), new Color(8, 56, 138)))
        );
        CustomSpectrum spectrum = Rainbow4J.readCustomSpectrum(image, colorClassifiers, new Rectangle(0, 0, 100, 100));


        assertThat(spectrum.getCollectedColors(), is(new HashMap<String, Integer>(){{
            put("green-blue", 5780);
        }}));
        assertThat(spectrum.getOtherColors(), is(4220));
        assertThat(spectrum.getTotalPixels(), is(10000));
    }

    @Test
    public void shouldRead_imageSpectrum_withPredefinedColorClassifiers_inSpecifiedArea_4() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/colo-scheme-gradient.png").getFile());

        List<ColorClassifier> colorClassifiers = asList(
                new SimpleColorClassifier("pink", new Color(252, 18, 53))
        );
        CustomSpectrum spectrum = Rainbow4J.readCustomSpectrum(image, colorClassifiers, new Rectangle(100, 0, 100, 100));

        assertThat(spectrum.getCollectedColors(), is(new HashMap<String, Integer>(){{
            put("pink", 10000);
        }}));
        assertThat(spectrum.getOtherColors(), is(0));
        assertThat(spectrum.getTotalPixels(), is(10000));
    }
    @Test
    public void shouldRead_imageSpectrum_fromPNG() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/test-spectrum-black-white-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image);

        Assert.assertEquals((int)spectrum.getPercentage(255,255,255, 0), 67);
        Assert.assertEquals((int)spectrum.getPercentage(0, 0, 0, 0), 30);
        Assert.assertEquals((int)spectrum.getPercentage(128,128,128, 0), 0);

        Assert.assertEquals((int)spectrum.getPercentage(254,254,254, 0), 0);
        Assert.assertEquals((int)spectrum.getPercentage(254,254,254, 1), 68);
        Assert.assertEquals((int)spectrum.getPercentage(254,250,254, 10), 68);
    }
    
    @Test(enabled = false)
    public void shouldRead_imageSpectrum_fromJPG() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/test-spectrum-black-white-1.jpg").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image);

        Assert.assertEquals((int)spectrum.getPercentage(255,255,255, 0), 67);
        Assert.assertEquals((int)spectrum.getPercentage(0, 0, 0, 0), 30);
        Assert.assertEquals((int)spectrum.getPercentage(128,128,128, 0), 0);

        Assert.assertEquals((int)spectrum.getPercentage(254,254,254, 0), 0);
        Assert.assertEquals((int)spectrum.getPercentage(254,254,254, 1), 68);
        Assert.assertEquals((int)spectrum.getPercentage(254,250,254, 10), 68);
    }


    @Test
    public void shouldRead_image_fromStream() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResourceAsStream("/color-scheme-image-1.png"));

        Assert.assertEquals(image.getWidth(), 778);
        Assert.assertEquals(image.getHeight(), 392);
    }
    
    @Test
    public void shouldRead_imageSpectrum_fromPNG_2() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/color-scheme-image-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image);

        Assert.assertEquals((int)spectrum.getPercentage(58, 112, 208, 5), 8);
        Assert.assertEquals((int)spectrum.getPercentage(207, 71, 29, 5), 32);
    }
    
    @Test(enabled = false)
    public void shouldRead_imageSpectrum_fromJPG_2() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/color-scheme-image-1.jpg").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image);

        Assert.assertEquals((int)spectrum.getPercentage(58, 112, 208, 5), 8);
        Assert.assertEquals((int)spectrum.getPercentage(207, 71, 29, 5), 32);
    }
    
    
    
    @Test
    public void shouldReadSpectrum_fromSpecifiedRegion() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/test-spectrum-black-white-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image, new Rectangle(100, 200, 20, 20));

        Assert.assertEquals((int)spectrum.getPercentage(255 ,255, 255, 0), 0);
        Assert.assertEquals((int)spectrum.getPercentage(0, 0, 0, 0), 100);
        Assert.assertEquals((int)spectrum.getPercentage(128,128,128, 0), 0);
    }
    
    @Test
    public void shouldCropImage_andReadSpectrum_2() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/color-scheme-image-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image, new Rectangle(10, 10, 30, 20));
        
        Assert.assertEquals((int)spectrum.getPercentage(255 ,255, 255, 0), 100);
        Assert.assertEquals((int)spectrum.getPercentage(0, 0, 0, 0), 0);
        Assert.assertEquals((int)spectrum.getPercentage(128,128,128, 0), 0);
    }

    @Test
    public void shouldReadSpectrum_fromPNG_3() throws IOException{
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/color-scheme-image-2.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image, new Rectangle(48, 217, 344, 407));

        Assert.assertEquals((int)spectrum.getPercentage(170, 170, 170, 5), 2);
        Assert.assertEquals((int)spectrum.getPercentage(119, 119, 119, 5), 1);
        Assert.assertEquals((int)spectrum.getPercentage(255, 255, 255, 5), 95);
    }
    
    @Test
    public void shouldGive_colorDistribution() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/color-scheme-image-1.png").getFile());

        Spectrum spectrum = Rainbow4J.readSpectrum(image);
        
        List<ColorDistribution> colors = spectrum.getColorDistribution(3);
        
        Assert.assertEquals(colors.size(), 4);
        
        Assert.assertEquals(colors.get(0).getColor(), new Color(0, 0, 0));
        Assert.assertEquals((int)colors.get(0).getPercentage(), 14);
        
        Assert.assertEquals(colors.get(1).getColor(), new Color(58, 112, 207));
        Assert.assertEquals((int)colors.get(1).getPercentage(), 8);
        
        Assert.assertEquals(colors.get(2).getColor(), new Color(207, 71, 29));
        Assert.assertEquals((int)colors.get(2).getPercentage(), 32);
        
        Assert.assertEquals(colors.get(3).getColor(), new Color(255, 255, 255));
        Assert.assertEquals((int)colors.get(3).getPercentage(), 44);
    }



    @Test(dataProvider = "imageCompareProvider")
    public void shouldCompare_images(int pixelSmooth, double approxPercentage, long expectedTotalPixels) throws IOException {
        BufferedImage imageA = Rainbow4J.loadImage(getClass().getResource("/comp-image-1.jpg").getFile());
        BufferedImage imageB = Rainbow4J.loadImage(getClass().getResource("/comp-image-2.jpg").getFile());

        ComparisonOptions options = new ComparisonOptions();
        if (pixelSmooth > 0) {
            options.addFilterBoth(new BlurFilter(pixelSmooth));
        }

        ImageCompareResult diff = Rainbow4J.compare(imageA, imageB, options);

        assertThat(diff.getPercentage(), is(greaterThan(approxPercentage - 0.02)));
        assertThat(diff.getPercentage(), is(lessThan(approxPercentage + 0.02)));

        assertThat(diff.getTotalPixels(), is(expectedTotalPixels));
    }

    @Test
    public void shouldCompare_sameImages_ofDifferentSizes() throws IOException {
        BufferedImage imageA = Rainbow4J.loadImage(getClass().getResource("/comp-image-1.jpg").getFile());
        BufferedImage imageB = Rainbow4J.loadImage(getClass().getResource("/comp-image-1-scaled-down.jpg").getFile());

        ComparisonOptions options = new ComparisonOptions();
        options.setStretchToFit(true);
        options.setTolerance(10);

        ImageCompareResult diff = Rainbow4J.compare(imageA, imageB, options);

        assertThat(diff.getTotalPixels(), is(6670L));
        assertThat(diff.getPercentage(), is(lessThan(2.86)));
        assertThat(diff.getPercentage(), is(greaterThan(2.56)));
    }

    @Test
    public void shouldCompare_differentImages_withDifferentSizes() throws IOException {
        BufferedImage imageA = Rainbow4J.loadImage(getClass().getResource("/comp-image-1.jpg").getFile());
        BufferedImage imageB = Rainbow4J.loadImage(getClass().getResource("/comp-image-3-scaled-down.jpg").getFile());

        ComparisonOptions options = new ComparisonOptions();
        options.setStretchToFit(true);
        options.setTolerance(10);
        ImageCompareResult diff = Rainbow4J.compare(imageA, imageB, options);

        assertThat(diff.getTotalPixels(), is(greaterThan(14800L)));
        assertThat(diff.getTotalPixels(), is(lessThan(15500L)));

        assertThat(diff.getPercentage(), is(greaterThan(5.9)));
        assertThat(diff.getPercentage(), is(lessThan(6.1)));
    }

    @Test
    public void shouldCompare_images_withOnlyPartialRegions() throws IOException {
        BufferedImage imageA = Rainbow4J.loadImage(getClass().getResource("/page-screenshot.png").getFile());
        BufferedImage imageB = Rainbow4J.loadImage(getClass().getResource("/page-sample-correct.png").getFile());

        ComparisonOptions options = new ComparisonOptions();
        options.setTolerance(2);

        ImageCompareResult diff = Rainbow4J.compare(imageA, imageB, new Rectangle(100, 90, 100, 40), new Rectangle(40, 40, 100, 40), options);

        assertThat(diff.getTotalPixels(), is(lessThan(2L)));
        assertThat(diff.getPercentage(), is(lessThan(0.01)));
    }


    @Test
    public void shouldCompare_images_andReturn_comparisonMap() throws IOException {
        BufferedImage imageA = Rainbow4J.loadImage(getClass().getResource("/page-screenshot-1.png").getFile());
        BufferedImage imageB = Rainbow4J.loadImage(getClass().getResource("/page-screenshot-1-sample-1.png").getFile());

        ComparisonOptions options = new ComparisonOptions();
        options.addFilterBoth(new BlurFilter(1));
        options.setTolerance(100);

        ImageCompareResult diff = Rainbow4J.compare(imageA, imageB, new Rectangle(0, 70, 100, 64), new Rectangle(0, 0, imageB.getWidth(), imageB.getHeight()), options);

        assertThat(diff.getComparisonMap(), is(notNullValue()));
    }

    @Test
    public void shouldSmoothImage() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/lenna.jpg").getFile());

        ImageHandler handler = new ImageHandler(image);
        handler.applyFilter(new BlurFilter(10), new Rectangle(0, 0, image.getWidth(), image.getHeight()));
    }

    @Test
    public void shouldRemoveNoiseImage() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResource("/denoise.png").getFile());

        ImageHandler handler = new ImageHandler(image);
        handler.applyFilter(new DenoiseFilter(10), new Rectangle(0, 0, image.getWidth(), image.getHeight()));
    }

    @Test
    public void shouldApplyContrast_toImage() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResourceAsStream("/lenna.jpg"));

        ImageHandler handler = new ImageHandler(image);
        handler.applyFilter(new ContrastFilter(200));
    }

    @Test
    public  void shouldApplySaturation_toImage() throws  IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResourceAsStream("/lenna.jpg"));

        ImageHandler handler = new ImageHandler(image);
        handler.applyFilter(new SaturationFilter(0));
    }

    @Test
    public void shouldApplyQuantinzation_toImage() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResourceAsStream("/lenna.jpg"));

        ImageHandler handler = new ImageHandler(image);
        handler.applyFilter(new QuantinizeFilter(2));
    }


    @Test
    public void shouldUseOffset_forDiffAnalysis() throws IOException {
        BufferedImage image = Rainbow4J.loadImage(getClass().getResourceAsStream("/lenna.png"));
        BufferedImage imageOffset = Rainbow4J.loadImage(getClass().getResourceAsStream("/lenna-offset.png"));

        // First comparing without offset
        {
            ImageCompareResult result = Rainbow4J.compare(image, imageOffset, new ComparisonOptions());
            assertThat(result.getTotalPixels(), is(allOf(greaterThan(64000L), lessThan(66000L))));
            assertThat(result.getOffsetX(), is(0));
            assertThat(result.getOffsetY(), is(0));
        }

        // Comparing without small offset
        {
            ComparisonOptions options2 = new ComparisonOptions();
            options2.setAnalyzeOffset(1);
            ImageCompareResult result2 = Rainbow4J.compare(image, imageOffset, options2);
            assertThat(result2.getTotalPixels(), is(allOf(greaterThan(63000L), lessThan(66000L))));
        }

        // Comparing without bigger offset
        {
            ComparisonOptions options3 = new ComparisonOptions();
            options3.setAnalyzeOffset(4);
            ImageCompareResult result3 = Rainbow4J.compare(image, imageOffset, options3);
            assertThat(result3.getTotalPixels(), is(0L));
            assertThat(result3.getOffsetX(), is(-2));
            assertThat(result3.getOffsetY(), is(-4));
        }
    }

    @Test
    public void shouldApply_maskFilter_andShouldGive_smallDifference() throws IOException {
        BufferedImage imageActual = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/actual.png"));
        BufferedImage imageExpected = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/expected-with-rect.png"));
        BufferedImage imageMask = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/mask.png"));

        ComparisonOptions options = new ComparisonOptions();
        List<ImageFilter> filters = new LinkedList<>();
        filters.add(new MaskFilter(new ImageHandler(imageMask)));
        options.setOriginalFilters(filters);

        ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);

        assertThat(result.getTotalPixels(), is(57L));
        assertThat(result.getPercentage(), is(lessThan(0.25)));
    }


    @Test
    public void shouldApply_maskFilter_andShouldGive_biggerDifference() throws IOException {
        BufferedImage imageActual = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/actual.png"));
        BufferedImage imageExpected = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/expected-with-rect-and-cross.png"));
        BufferedImage imageMask = Rainbow4J.loadImage(getClass().getResourceAsStream("/mask/mask.png"));

        ComparisonOptions options = new ComparisonOptions();
        List<ImageFilter> filters = new LinkedList<>();
        filters.add(new MaskFilter(new ImageHandler(imageMask)));
        options.setOriginalFilters(filters);

        ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);

        assertThat(result.getTotalPixels(), is(7907L));
        assertThat(result.getPercentage(), is(lessThan(30.0)));
        assertThat(result.getPercentage(), is(greaterThan(28.0)));
    }

    @Test
    public void shouldApply_denoiseFilter_andRemoveAllNoisePixels() throws IOException {
        BufferedImage imageActual = Rainbow4J.loadImage(getClass().getResourceAsStream("/noise/menu-item-1.png"));
        BufferedImage imageExpected = Rainbow4J.loadImage(getClass().getResourceAsStream("/noise/menu-item-1-expected-spots-2.png"));


        // Assert first that there are a lot of mismatching pixels
        {
            ComparisonOptions options = new ComparisonOptions();
            options.setTolerance(25);
            ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);
            assertThat(result.getTotalPixels(), is(1075L));
        }

        List<Integer> expectedPixels = asList(
                603, 499, 496, 483, 458, 427, 388, 385, 383
        );

        for (int size = 1; size < 10; size++) {
            ComparisonOptions options = new ComparisonOptions();
            options.setTolerance(25);
            options.setMapFilters(asList((ImageFilter)new DenoiseFilter(size)));
            ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);

            assertThat("map-denoise " + size + " should result in mismatching pixels",
                    result.getTotalPixels(),
                    is(expectedPixels.get(size - 1).longValue()));
        }
    }

    @Test
    public void shouldApply_blurFilter_andImproveNoisyDiff() throws IOException {
        BufferedImage imageActual = Rainbow4J.loadImage(getClass().getResourceAsStream("/noise/menu-item-1.png"));
        BufferedImage imageExpected = Rainbow4J.loadImage(getClass().getResourceAsStream("/noise/menu-item-1-expected-spots-2.png"));

        List<Integer> expectedPixels = asList(
                653, 765, 860, 982, 1068, 1168, 1263, 1334, 1415
        );

        // Assert first that there are a lot of mismatching pixels
        {
            ComparisonOptions options = new ComparisonOptions();
            options.setTolerance(25);
            ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);
            assertThat(result.getTotalPixels(), is(1075L));
        }

        for (int size = 1; size < 10; size++) {
            ComparisonOptions options = new ComparisonOptions();
            options.setTolerance(25);
            List<ImageFilter> filters = new LinkedList<>();
            filters.add(new BlurFilter(size));
            options.setOriginalFilters(filters);
            options.setSampleFilters(filters);

            ImageCompareResult result = Rainbow4J.compare(imageActual, imageExpected, options);

            assertThat("blur filter with size " + size + " should result in mismatching pixels",
                    result.getTotalPixels(),
                    is(expectedPixels.get(size - 1).longValue()));
        }
    }


    @DataProvider
    public Object[][] imageCompareProvider() {
        return new Object[][] {
                //pixelsmooth,  approx percentage, total pixels
                {0, 0.72, 1797},
                {1, 0.85, 2138},
                {2, 0.87, 2176},
                {3, 0.92, 2305}
        };
    }


}
