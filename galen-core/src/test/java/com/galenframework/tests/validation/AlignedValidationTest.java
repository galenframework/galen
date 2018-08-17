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
import com.galenframework.specs.Alignment;
import com.galenframework.specs.SpecHorizontally;
import com.galenframework.specs.SpecVertically;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;


public class AlignedValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Aligned Horizontally

            {specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(20, 10, 10, 10));
            }})},

            {specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 15, 10, 10));
                put("item",  element(20, 10, 10, 20));
            }})},

            {specHorizontally(Alignment.TOP, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(20, 10, 10, 20));
            }})},

            {specHorizontally(Alignment.BOTTOM, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 40, 10, 10));
                put("item",  element(20, 30, 10, 20));
            }})},

            {specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 20));
                put("item",  element(20, 10, 10, 20));
            }})},

            {specHorizontally(Alignment.ALL, "item", 1), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 20));
                put("item",  element(20, 11, 10, 21));
            }})},

            // Vertically

            {specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(10, 20, 10, 10));
            }})},

            {specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(15, 10, 10, 10));
                put("item",  element(10, 20, 20, 10));
            }})},

            {specVertically(Alignment.LEFT, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(10, 20, 20, 10));
            }})},

            {specVertically(Alignment.RIGHT, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(30, 10, 10, 10));
                put("item",  element(20, 20, 20, 10));
            }})},

            {specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(10, 20, 10, 10));
            }})},

            {specVertically(Alignment.ALL, "item", 1), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("item",  element(11, 20, 11, 10));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Horizontally

            {validationResult(NO_AREA, messages("Cannot find locator for \"item\" in page spec"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
            }})},

            {validationResult(NO_AREA, messages("\"item\" is not visible on page"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", invisibleElement(10, 10, 10, 20));
            }})},

            {validationResult(NO_AREA, messages("\"item\" is absent on page"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", absentElement(10, 10, 10, 15));
            }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("item", element(10, 10, 50, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 15));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 15));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 50, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 15), "item")),
                    messages("\"item\" is not aligned horizontally centered with \"object\". Offset is 2px"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 15));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 50, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 20), "item")),
                    messages("\"item\" is not aligned horizontally centered with \"object\". Offset is 5px"), NULL_META),
                specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 20));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 15, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 20), "item")),
                    messages("\"item\" is not aligned horizontally top with \"object\". Offset is 5px"), NULL_META),
                specHorizontally(Alignment.TOP, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 15, 10, 10));
                    put("item", element(10, 10, 10, 20));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 5), "item")),
                    messages("\"item\" is not aligned horizontally bottom with \"object\". Offset is 5px"), NULL_META),
                specHorizontally(Alignment.BOTTOM, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("item", element(10, 10, 10, 5));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(30, 10, 10, 5), "item")),
                    messages("\"item\" is not aligned horizontally all with \"object\". Offset is 5px"), NULL_META),
                specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("item", element(30, 10, 10, 5));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(30, 10, 15, 5), "item")),
                    messages("\"item\" is not aligned horizontally all with \"object\". Offset is 5px"), NULL_META),
                specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("item", element(30, 10, 15, 5));
            }})},

            // Aligned Vertically

            {validationResult(NO_AREA, messages("Cannot find locator for \"item\" in page spec"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
            }})},

            {validationResult(NO_AREA, messages("\"item\" is not visible on page"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", invisibleElement(10, 10, 10, 20));
            }})},

            {validationResult(NO_AREA, messages("\"item\" is absent on page"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 50, 10));
                    put("item", absentElement(10, 10, 10, 15));
            }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("item", element(10, 10, 50, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 20));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 50, 10));
                    put("item", element(10, 10, 10, 20));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(10, 20, 10, 10), "item")),
                    messages("\"item\" is not aligned vertically centered with \"object\". Offset is 5px"), NULL_META),
                specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 20, 10));
                    put("item", element(10, 20, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(5, 20, 10, 10), "item")),
                    messages("\"item\" is not aligned vertically left with \"object\". Offset is 5px"), NULL_META),
                specVertically(Alignment.LEFT, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 20, 10));
                    put("item", element(5, 20, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(10, 30, 10, 10), "item")),
                    messages("\"item\" is not aligned vertically right with \"object\". Offset is 10px"), NULL_META),
                specVertically(Alignment.RIGHT, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 20, 10));
                    put("item", element(10, 30, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 30, 5, 10), "item")),
                    messages("\"item\" is not aligned vertically all with \"object\". Offset is 5px"), NULL_META),
                specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("item", element(10, 30, 5, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(15, 30, 5, 10), "item")),
                    messages("\"item\" is not aligned vertically all with \"object\". Offset is 5px"), NULL_META),
                specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10));
                    put("item", element(15, 30,   5, 10));
            }})}
        };
    }

    private SpecVertically specVertically(Alignment alignment, String objectName, int errorRate) {
        SpecVertically spec = new SpecVertically(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }

    private SpecHorizontally specHorizontally(Alignment alignment, String objectName, int errorRate) {
        SpecHorizontally spec =  new SpecHorizontally(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }
}
