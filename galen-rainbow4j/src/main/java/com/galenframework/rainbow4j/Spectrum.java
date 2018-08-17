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

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.*;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static java.util.Arrays.asList;

public class WidthAndHeightValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Width

            {specWidth(exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 20, 50));
            }})},

            {specWidth(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 20, 50));
            }})},

            {specWidth(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 30, 50));
            }})},

            {specWidth(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 25, 50));
            }})},

            {specWidth(exact(50).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 15, 50));
                put("container", element(305, 400, 30, 50));
            }})},

            {specWidth(between(45, 55).withPercentOf("main-container-1/width")), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 15, 50));
                put("main-container-1", element(305, 400, 30, 50));
            }})},

            {specWidth(exact(new RangeValue(333, 1)).withPercentOf("main-container-1/width")), page(new HashMap<String, PageElement>(){{
                put("object", element(0, 0, 100, 50));
                put("main-container-1", element(0, 0, 300, 50));
            }})},

            // Height

            {specHeight(exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 60, 20));
            }})},

            {specHeight(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 60, 20));
            }})},

            {specHeight(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 60, 30));
            }})},

            {specHeight(between(20, 30)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 65, 25));
            }})},

            {specHeight(exact(50).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                put("object", element(100, 140, 65, 20));
                put("container", element(305, 140, 65, 40));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Width

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specWidth(exact(10)), page(new HashMap<String, PageElement>())
            },

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(310, 250, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(310, 250, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px instead of 10px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "10px", "100px"))),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be greater than 110px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "> 110px", "100px"))),
                specWidth(Range.greaterThan(110)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be less than 90px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "< 90px", "100px"))),
                specWidth(Range.lessThan(90)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px which is not in range of 10 to 40px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "10 to 40px", "100px"))),
                specWidth(between(10, 40)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] instead of 10% [20px]"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "10% [20px]", "50% [100px]"))),
                specWidth(exact(10).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 200, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] which is not in range of 10 to 20% [20 to 40px]"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "object", Side.RIGHT, "10 to 20% [20 to 40px]", "50% [100px]"))),
                specWidth(between(10, 20).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 200, 50));
            }})},


            // Height

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specHeight(exact(10)), page(new HashMap<String, PageElement>())
            },

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(310, 250, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(310, 250, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px instead of 10px"),
                    asList(LayoutMeta.distance("object", Side.TOP, "object", Side.BOTTOM, "10px", "50px"))),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px which is not in range of 10 to 40px"),
                    asList(LayoutMeta.distance("object", Side.TOP, "object", Side.BOTTOM, "10 to 40px", "50px"))),
                specHeight(between(10, 40)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] instead of 10% [20px]"),
                    asList(LayoutMeta.distance("object", Side.TOP, "object", Side.BOTTOM, "10% [20px]", "25% [50px]"))),
                specHeight(exact(10).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 100, 200));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] which is not in range of 10 to 15% [20 to 30px]"),
                    asList(LayoutMeta.distance("object", Side.TOP, "object", Side.BOTTOM, "10 to 15% [20 to 30px]", "25% [50px]"))),
                specHeight(between(10, 15).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 100, 200));
            }})}
        };
    }

    private SpecHeight specHeight(Range range) {
        return new SpecHeight(range);
    }

    private SpecWidth specWidth(Range range) {
        return new SpecWidth(range);
    }

}
package com.galenframework.rainbow4j;

import com.galenframework.rainbow4j.colorscheme.ColorDistribution;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class Spectrum {

    private final int[][][] data;
    private int pixelsAmount;
    private int precision;

    public Spectrum(int[][][] data, int width, int height) {
        this.precision = data.length;
        this.data = data;
        this.pixelsAmount = width * height;
    }

    /**
     * 
     * @param red 0 to 255 value of red
     * @param green 0 to 255 value of green
     * @param blue 0 to 255 value of blue
     * @param range 0 to 255 value of range within which it should take histogram value
     * @return
     */
    public double getPercentage(int red, int green, int blue, int range) {

        long counter = 0;
        
        int cr = Math.min(red * precision / 256, precision - 1);
        int cg = Math.min(green * precision / 256, precision - 1);
        int cb = Math.min(blue * precision / 256, precision - 1);
        
        int crange = Math.min(range * precision / 256, precision - 1);
        
        
        int rRange[] = new int[]{Math.max(0, cr - crange), Math.min(cr + crange, precision - 1)};
        int gRange[] = new int[]{Math.max(0, cg - crange), Math.min(cg + crange, precision - 1)};
        int bRange[] = new int[]{Math.max(0, cb - crange), Math.min(cb + crange, precision - 1)};
        
        for (int ir = rRange[0]; ir <= rRange[1]; ir++) {
            for (int ig = gRange[0]; ig <= gRange[1]; ig++) {
                for (int ib = bRange[0]; ib <= bRange[1]; ib++) {
                    counter += data[ir][ig][ib];
                }
            }
        }

        return 100.d * counter/pixelsAmount;
    }
    
    public void printColors() {
        for (int r = 0; r<precision; r++) {
            for (int g = 0; g<precision; g++) {
                for (int b = 0; b<precision; b++) {
                    if (data[r][g][b] > 0) {
                        System.out.println(String.format("(%d, %d, %d) = %d", r, g, b, data[r][g][b]));
                    }
                }
            }
        }
    }

    public int getPrecision() {
        return precision;
    }

    public List<ColorDistribution> getColorDistribution(int minPercentage) {
        double usage = 0;
        
        List<ColorDistribution> colors = new LinkedList<>();
        for (int r = 0; r<precision; r++) {
            for (int g = 0; g<precision; g++) {
                for (int b = 0; b<precision; b++) {
                    usage = data[r][g][b] * 100 / pixelsAmount;
                    
                    if (usage >= minPercentage) {
                        colors.add(new ColorDistribution(new Color(r, g, b), usage));
                    }
                }
            }
        }
        return colors;
    }
}
