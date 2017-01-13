/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.colorscheme.SimpleColorClassifier;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.SpecColorScheme;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static java.util.Arrays.asList;

public class ColorSchemeValidationTest extends ValidationTestBase {
    private BufferedImage testImage = loadTestImage("/color-scheme-image-1.png");

    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][] {
            {specColorScheme(new ColorRange("white", new SimpleColorClassifier("white", Color.white), between(46, 52)),
                new ColorRange("black", new SimpleColorClassifier("black", Color.black), between(34, 40))), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 400, 300));
            }}, testImage)},

            {specColorScheme(new ColorRange("white", new SimpleColorClassifier("white", Color.white), exact(100))), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 50, 40));
            }}, testImage)},
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][] {
            {validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                specColorScheme(new ColorRange("black", new SimpleColorClassifier("black", Color.black), between(30, 33))), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 400, 300));
                }}, testImage)},
            {validationResult(NO_AREA, messages("\"object\" is absent on page")),
                specColorScheme(new ColorRange("black", new SimpleColorClassifier("black", Color.black), between(30, 33))), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 400, 300));
                }}, testImage)},
            {validationResult(areas(new ValidationObject(new Rect(10, 10, 400, 300), "object")),
                    messages("color black on \"object\" is 36% which is not in range of 10 to 20%")),
                specColorScheme(new ColorRange("black", new SimpleColorClassifier("black", Color.black), between(10, 20))), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 400, 300));
                }}, testImage)},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 400, 300), "object")),
                    messages("color white on \"object\" is 48% instead of 30%")),
                specColorScheme(new ColorRange("white", new SimpleColorClassifier("white", Color.white), exact(30))), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 400, 300));
                }}, testImage)},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 300), "object")),
                    messages("color #3A70D0 on \"object\" is 12% instead of 30%")),
                specColorScheme(new ColorRange("#3A70D0", new SimpleColorClassifier("#3A70D0", Color.decode("#3A70D0")), exact(30))), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 300));
                }}, testImage)},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 300), "object")),
                    messages("color #3A70D0 on \"object\" is 12.87% instead of 12.84%")),
                specColorScheme(new ColorRange("#3A70D0", new SimpleColorClassifier("#3A70D0", Color.decode("#3A70D0")), exact(new RangeValue(1284, 2)))), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 300));
                }}, testImage)}
        };
    }

    private SpecColorScheme specColorScheme(ColorRange...colorRanges) {
        SpecColorScheme spec = new SpecColorScheme();
        spec.setColorRanges(asList(colorRanges));
        return spec;
    }

}
