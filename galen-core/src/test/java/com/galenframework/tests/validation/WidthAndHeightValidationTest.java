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
import com.galenframework.specs.Range;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.SpecHeight;
import com.galenframework.specs.SpecWidth;
import org.testng.annotations.DataProvider;

import java.util.Collections;
import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static java.util.Collections.emptyList;

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

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), emptyList()),
                specWidth(exact(10)), page(new HashMap<String, PageElement>())
            },

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), emptyList()),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(310, 250, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), emptyList()),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(310, 250, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px instead of 10px"), emptyList()),
                specWidth(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be greater than 110px"), emptyList()),
                specWidth(Range.greaterThan(110)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be less than 90px"), emptyList()),
                specWidth(Range.lessThan(90)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px which is not in range of 10 to 40px"), emptyList()),
                specWidth(between(10, 40)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] instead of 10% [20px]"), emptyList()),
                specWidth(exact(10).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 200, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] which is not in range of 10 to 20% [20 to 40px]"), emptyList()),
                specWidth(between(10, 20).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 200, 50));
            }})},


            // Height

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), emptyList()),
                specHeight(exact(10)), page(new HashMap<String, PageElement>())
            },

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), emptyList()),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(310, 250, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), emptyList()),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(310, 250, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px instead of 10px"), emptyList()),
                specHeight(exact(10)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px which is not in range of 10 to 40px"), emptyList()),
                specHeight(between(10, 40)), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] instead of 10% [20px]"), emptyList()),
                specHeight(exact(10).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                    put("object", element(100, 100, 100, 50));
                    put("container", element(100, 100, 100, 200));
            }})},

            {validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] which is not in range of 10 to 15% [20 to 30px]"), emptyList()),
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
