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
import com.galenframework.specs.SpecCentered;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;


public class CenteredValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Centered Inside

            {specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 80, 80));
                put("container",  element(0, 0, 100, 100));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 81, 81));
                put("container",  element(0, 0, 100, 100));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
                put("object", element(9, 9, 80, 80));
                put("container",  element(0, 0, 100, 100));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY).withErrorRate(2), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 80, 20));
                put("container",  element(0, 0, 100, 100));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 30), page(new HashMap<String, PageElement>(){{
                put("object", element(60, 10, 50, 20));
                put("container",  element(0, 0, 200, 200));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 30), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 80, 20));
                put("container",  element(0, 0, 100, 200));
            }})},

            {specCenteredInside("container", SpecCentered.Alignment.VERTICALLY).withErrorRate(2), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 20, 80));
                put("container",  element(0, 0, 100, 100));
            }})},


            // Centered on

            {specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                put("object", element(80, 80, 90, 90));
                put("button",  element(100, 100, 50, 50));
            }})},

            {specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                put("object", element(81, 81, 90, 90));
                put("button",  element(100, 100, 50, 50));
            }})},

            {specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                put("object", element(80, 80, 89, 91));
                put("button",  element(100, 100, 50, 50));
            }})},

            {specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                put("object", element(80, 80, 90, 90));
                put("button",  element(100, 100, 50, 50));
            }})},

            {specCenteredOn("button", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                put("object", element(80, 80, 10, 90));
                put("button",  element(100, 100, 50, 50));
            }})},

            {specCenteredOn("button", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                put("object", element(80, 80, 90, 10));
                put("button",  element(100, 100, 50, 50));
            }})},
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Centered

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 10, 10));
                    put("container", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 10, 10));
                    put("container", element(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"container\" is absent on page"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("container", absentElement(10, 60, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"container\" is not visible on page"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 10, 10));
                    put("container", invisibleElement(10, 60, 10, 10));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 20, 80, 60), "object"), new ValidationObject(new Rect(0, 0, 100, 100), "container")),
                    messages("\"object\" is not centered horizontally inside \"container\". Offset is 20px"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 20, 80, 60));
                    put("container", element(0, 0, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 20, 75, 60), "object"), new ValidationObject(new Rect(0, 0, 100, 100), "container")),
                    messages("\"object\" is not centered horizontally inside \"container\". Offset is 15px"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 10), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 20, 75, 60));
                    put("container", element(0, 0, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(0, 20, 120, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is centered but not horizontally inside \"container\""), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                    put("object", element(0, 20, 120, 60));
                    put("container", element(10, 10, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 10, 100, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is not centered vertically inside \"container\". Offset is 40px"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 10, 100, 60));
                    put("container", element(10, 10, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is not centered horizontally inside \"container\". Offset is 70px"), NULL_META),
                specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 10, 10, 60));
                    put("container", element(10, 10, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is not centered vertically on \"container\". Offset is 40px"), NULL_META),
                specCenteredOn("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 10, 10, 60));
                    put("container", element(10, 10, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is not centered horizontally on \"container\". Offset is 70px"), NULL_META),
                specCenteredOn("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                    put("object", element(20, 10, 10, 60));
                    put("container", element(10, 10, 100, 100));
            }})}
        };
    }

    private SpecCentered specCenteredOn(String object, SpecCentered.Alignment alignment) {
        return new SpecCentered(object, alignment, SpecCentered.Location.ON).withErrorRate(2);
    }

    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE);
    }

    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment, int errorRate) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE).withErrorRate(errorRate);
    }
}
