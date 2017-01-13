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
import com.galenframework.specs.Alignment;
import com.galenframework.specs.Location;
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecNear;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static com.galenframework.specs.Side.*;
import static java.util.Arrays.asList;

public class NearValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Near

            {specNear("button", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(90, 140, 100, 50));
                put("button", element(200, 200, 100, 50));
            }})},

            {specNear("button", location(between(5, 12), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(90, 140, 100, 50));
                put("button", element(200, 200, 100, 50));
            }})},

            {specNear("button", location(between(5, 20), RIGHT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                put("object", element(310, 260, 100, 50));
                put("button", element(200, 200, 100, 50));
            }})},

            {specNear("button", location(exact(5), RIGHT), location(between(5, 15), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(305, 140, 100, 50));
                put("button", element(200, 200, 100, 50));
            }})},

            {specNear("button", location(exact(100).withPercentOf("button/width"), RIGHT)), page(new HashMap<String, PageElement>(){{
                put("object", element(200, 140, 100, 50));
                put("button", element(100, 100, 50, 50));
            }})},

            {specNear("button", location(between(95, 105).withPercentOf("button/width"), RIGHT)), page(new HashMap<String, PageElement>(){{
                put("object", element(200, 140, 100, 50));
                put("button", element(100, 100, 50, 50));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Near
            {validationResult(areas(new ValidationObject(new Rect(90, 5, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                    messages("\"object\" is 10px left instead of 30px")),
                specNear("button", location(exact(30), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(90, 5, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(90, 5, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                    messages("\"object\" is 10px left which is not in range of 20 to 30px")),
                specNear("button", location(between(20, 30), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(90, 5, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                    messages("\"object\" is 10px left and 20px top instead of 30px")),
                specNear("button", location(exact(30), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(90, 130, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(310, 250, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                    messages("\"object\" is 10px right instead of 30px, is 0px bottom which is not in range of 10 to 20px")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(310, 250, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 50, 50), "button")),
                    messages("\"object\" is 20% [10px] left instead of 40% [20px]")),
                specNear("button", location(exact(40).withPercentOf("button/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(90, 130, 100, 50));
                    put("button", element(200, 200, 50, 50));
            }})},

            {validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 50, 50), "button")),
                    messages("\"object\" is 20% [10px] left which is not in range of 40 to 50% [20 to 25px]")),
                specNear("button", location(between(40, 50).withPercentOf("button/area/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(90, 130, 100, 50));
                    put("button", element(200, 200, 50, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(310, 250, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(310, 250, 100, 50));
                    put("button", element(200, 200, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is absent on page")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(310, 250, 100, 50));
                    put("button", absentElement(200, 200, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(310, 250, 100, 50));
                    put("button", invisibleElement(200, 200, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"button\" in page spec")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(310, 250, 100, 50));
            }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("button", absentElement(200, 200, 100, 50));
            }})}
        };
    }

    private SpecNear specNear(String secondObjectName, Location...locations) {
        return new SpecNear(secondObjectName, asList(locations));
    }

}
