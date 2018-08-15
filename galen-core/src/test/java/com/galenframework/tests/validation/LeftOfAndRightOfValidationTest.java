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
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Range;
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecLeftOf;
import com.galenframework.specs.SpecRightOf;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static java.util.Arrays.asList;

public class LeftOfAndRightOfValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Left of

            {specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(40, 10, 10, 10));
            }})},

            {specLeftOf("button", between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(43, 10, 10, 10));
            }})},

            // Right of

            {specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(40, 10, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})},

            {specRightOf("button", between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(43, 10, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Left of

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is not visible on page"), NULL_META),
                specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", invisibleElement(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is absent on page"), NULL_META),
                specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", absentElement(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(60, 10, 10, 10), "button")),
                    messages("\"object\" is 40px left of \"button\" instead of 20px"),
                    asList(LayoutMeta.distance("object", Side.RIGHT, "button", Side.LEFT, "20px", "40px"))),
                specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("button", element(60, 10, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(60, 10, 10, 10), "button")),
                    messages("\"object\" is 40px left of \"button\" which is not in range of 20 to 30px"),
                    asList(LayoutMeta.distance("object", Side.RIGHT, "button", Side.LEFT, "20 to 30px", "40px"))),
                specLeftOf("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("button", element(60, 10, 10, 10));
            }})},

            // Right of

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is not visible on page"), NULL_META),
                specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", invisibleElement(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is absent on page"), NULL_META),
                specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", absentElement(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(60, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 10), "button")),
                    messages("\"object\" is 40px right of \"button\" instead of 20px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "button", Side.RIGHT, "20px", "40px"))),
                specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(60, 10, 10, 10));
                    put("button", element(10, 10, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(60, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 10), "button")),
                    messages("\"object\" is 40px right of \"button\" which is not in range of 20 to 30px"),
                    asList(LayoutMeta.distance("object", Side.LEFT, "button", Side.RIGHT, "20 to 30px", "40px"))),
                specRightOf("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                    put("object", element(60, 10, 10, 10));
                    put("button", element(10, 10, 10, 10));
            }})}
        };
    }

    private SpecLeftOf specLeftOf(String object, Range range) {
        return new SpecLeftOf(object, range);
    }

    private SpecRightOf specRightOf(String object, Range range) {
        return new SpecRightOf(object, range);
    }

}
