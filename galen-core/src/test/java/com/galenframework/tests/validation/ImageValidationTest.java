/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.validation;

import com.galenframework.components.validation.MockedPage;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.filters.BlurFilter;
import com.galenframework.rainbow4j.filters.ImageFilter;
import com.galenframework.specs.SpecImage;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ImageValidationTest extends ValidationTestBase {
    private static final boolean PIXEL_UNIT = true;
    private static final boolean PERCENTAGE_UNIT = false;

    private BufferedImage imageComparisonTestScreenshot = loadTestImage("/imgs/page-screenshot.png");
    private BufferedImage testImage = loadTestImage("/color-scheme-image-1.png");

    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][] {
            {specImage(asList("/imgs/button-sample-correct.png"), 1, PIXEL_UNIT, 0, 5), page(new HashMap<String, PageElement>(){{
                put("object", element(100, 90, 100, 40));
            }}, imageComparisonTestScreenshot)},

            {specImage(asList("/imgs/page-sample-correct.png"), 2, PIXEL_UNIT, 0, 5, new Rect(40, 40, 100, 40)), page(new HashMap<String, PageElement>() {{
                put("object", element(100, 90, 100, 40));
            }}, imageComparisonTestScreenshot)},

            {specImage(asList("/imgs/button-sample-incorrect.png", "/imgs/button-sample-correct.png"), 1, PIXEL_UNIT, 0, 5), page(new HashMap<String, PageElement>(){{
                put("object", element(100, 90, 100, 40));
            }}, imageComparisonTestScreenshot)},

            {specImage(asList("/imgs/button-sample-*.png"), 1, PIXEL_UNIT, 0, 5), page(new HashMap<String, PageElement>(){{
                put("object", element(100, 90, 100, 40));
            }}, imageComparisonTestScreenshot)},
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][] {
            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, true, 0, 10), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 400, 300));
                }}, testImage)},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, true, 0, 10), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 400, 300));
                }}, testImage)},

            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                    new ValidationError(messages("Element does not look like \"/imgs/button-sample-incorrect.png\". " +
                        "There are 3820 mismatching pixels but max allowed is 600")), NULL_META),
                specImage(asList("/imgs/button-sample-incorrect.png"), 600, PIXEL_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                    put("object", element(100, 90, 100, 40));
                }}, imageComparisonTestScreenshot)},

            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                    new ValidationError(messages("Element does not look like \"/imgs/button-sample-incorrect.png\". " +
                        "There are 95.5% mismatching pixels but max allowed is 2%")), NULL_META),
                specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, PERCENTAGE_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                    put("object", element(100, 90, 100, 40));
                }}, imageComparisonTestScreenshot)},

            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                    new ValidationError(messages("Couldn't load image: /imgs/undefined-image.png")), NULL_META),
                specImage(asList("/imgs/undefined-image.png"), 1.452, PERCENTAGE_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                    put("object", element(100, 90, 100, 40));
                }}, imageComparisonTestScreenshot)}
        };
    }

    @Test
    public void imageSpec_shouldAlsoGenerate_imageComparisonMap() {
        MockedPage page = page(new HashMap<String, PageElement>() {{
            put("object", element(100, 90, 100, 40));
        }}, imageComparisonTestScreenshot);

        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("object", specImage(asList("/imgs/button-sample-incorrect.png"), 0, PIXEL_UNIT, 0, 10)).getError();

        assertThat("Comparison map should not be null", error.getImageComparison().getComparisonMap(), is(notNullValue()));
    }

    private SpecImage specImage(List<String> imagePaths, double errorValue, boolean isPixelUnit, int pixelSmooth, int tolerance) {
        return specImage(imagePaths, errorValue, isPixelUnit, pixelSmooth, tolerance, null);
    }

    private SpecImage specImage(List<String> imagePaths, double errorValue, boolean isPixelUnit, int blur, int tolerance, Rect selectedArea) {
        SpecImage spec = new SpecImage();

        if (isPixelUnit) {
            spec.setErrorRate(new SpecImage.ErrorRate(errorValue, SpecImage.ErrorRateType.PIXELS));
        }
        else {
            spec.setErrorRate(new SpecImage.ErrorRate(errorValue, SpecImage.ErrorRateType.PERCENT));
        }

        spec.setImagePaths(imagePaths);

        List<ImageFilter> filters = new LinkedList<>();
        spec.setOriginalFilters(filters);
        spec.setSampleFilters(filters);

        if (blur > 0) {
            filters.add(new BlurFilter(blur));
        }
        spec.setTolerance(tolerance);
        spec.setSelectedArea(selectedArea);
        return spec;
    }
}
