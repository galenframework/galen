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
package com.galenframework.tests.validation;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.Range;
import com.galenframework.specs.SpecAbove;
import com.galenframework.specs.SpecBelow;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;

public class AboveAndBelowValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][] {
            // Above
            {specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(10, 40, 10, 10));
            }})},

            {specAbove("button", between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(10, 42, 10, 10));
            }})},


            // Below
            {specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 40, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})},

            {specBelow("button", between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 42, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})},
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][] {
            // Above

            {validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page")),
                specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", invisibleElement(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is absent on page")),
                specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", absentElement(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 40, 10, 10), "object"), new ValidationObject(new Rect(10, 60, 10, 10), "button")),
                    messages("\"object\" is 10px above \"button\" instead of 20px")),
                specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 40, 10, 10), "object"), new ValidationObject(new Rect(10, 60, 10, 10), "button")),
                    messages("\"object\" is 10px above \"button\" which is not in range of 20 to 30px")),
                specAbove("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            // Below

            {validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page")),
                specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 10, 10));
                    put("button", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", invisibleElement(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is absent on page")),
                specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("button", absentElement(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                    messages("\"object\" is 10px below \"button\" instead of 20px")),
                specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 60, 10, 10));
                    put("button", element(10, 40, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                    messages("\"object\" is 10px below \"button\" which is not in range of 20 to 30px")),
                specBelow("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 60, 10, 10));
                    put("button", element(10, 40, 10, 10));
            }})}
        };
    }

    private SpecAbove specAbove(String object, Range range) {
        return new SpecAbove(object, range);
    }

    private SpecBelow specBelow(String object, Range range) {
        return new SpecBelow(object, range);
    }
}
