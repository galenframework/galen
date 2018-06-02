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
import com.galenframework.specs.Location;
import com.galenframework.specs.Side;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static com.galenframework.specs.Side.*;
import com.galenframework.specs.SpecOn;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class OnValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            {specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                put("object", element(90, 110, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})},

            {specOn(TOP, LEFT, "container", location(exact(10), RIGHT), location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(110, 90, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})},

            {specOn(TOP, LEFT, "container", location(exact(90), RIGHT), location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
                put("object", element(190, 110, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})},

            {specOn(TOP, LEFT, "container", location(exact(90), RIGHT), location(exact(20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                put("object", element(190, 120, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})},

            {specOn(BOTTOM, RIGHT, "container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(190, 180, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})},

            {specOn(BOTTOM, RIGHT, "container", location(exact(10), RIGHT), location(exact(20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                put("object", element(210, 220, 50, 50));
                put("container", element(100, 100, 100, 100));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), emptyList()),
                specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 40, 50, 50));
                    put("container", element(100, 100, 100, 100));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), emptyList()),
                specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 40, 50, 50));
                    put("container", element(100, 100, 100, 100));
            }})},

            {validationResult(NO_AREA, messages("\"container\" is not visible on page"), emptyList()),
                specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 50, 50));
                    put("container", invisibleElement(100, 100, 100, 100));
            }})},

            {validationResult(NO_AREA, messages("\"container\" is absent on page"), emptyList()),
                specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 40, 50, 50));
                    put("container", absentElement(100, 100, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(95, 110, 50, 50), "object"), new ValidationObject(new Rect(100, 100, 100, 100), "container")),
                    messages("\"object\" is 5px left instead of 10px"), emptyList()),
                specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(95, 110, 50, 50));
                    put("container", element(100, 100, 100, 100));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(105, 90, 50, 50), "object"), new ValidationObject(new Rect(100, 100, 100, 100), "container")),
                    messages("\"object\" is 5px right which is not in range of 10 to 15px, is 10px top instead of 5px"), emptyList()),
                specOn(TOP, LEFT, "container", location(between(10, 15), RIGHT), location(exact(5), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(105, 90, 50, 50));
                    put("container", element(100, 100, 100, 100));
            }})}
        };
    }

    private SpecOn specOn(Side sideHorizontal, Side sideVertical, String parentObjectName, Location...locations) {
        return new SpecOn(parentObjectName, sideHorizontal, sideVertical, asList(locations));
    }

}
