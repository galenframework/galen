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

import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.SpecCss;
import com.galenframework.specs.SpecText;
import org.testng.annotations.DataProvider;

import java.util.HashMap;


public class CssValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][] {
            {new SpecCss("font-size", SpecText.Type.IS, "18px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})},

            {new SpecCss("font-size", SpecText.Type.ENDS, "px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})},

            {new SpecCss("font-size", SpecText.Type.STARTS, "18"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})},

            {new SpecCss("font-size", SpecText.Type.CONTAINS, "8p"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})},

            {new SpecCss("font-size", SpecText.Type.MATCHES, "[0-9]+px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][] {
            // Css
            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                page(new HashMap<>())
            },

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 10, 10));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should be \"19px\""), NULL_META),
                new SpecCss("font-size", SpecText.Type.IS, "19px"),
                page(new HashMap<String, PageElement>(){{
                    put("object", elementWithCss("font-size", "18px"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should start with \"19\""), NULL_META),
                new SpecCss("font-size", SpecText.Type.STARTS, "19"),
                page(new HashMap<String, PageElement>(){{
                    put("object", elementWithCss("font-size", "18px"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should end with \"em\""), NULL_META),
                new SpecCss("font-size", SpecText.Type.ENDS, "em"),
                page(new HashMap<String, PageElement>(){{
                    put("object", elementWithCss("font-size", "18px"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should contain \"9\""), NULL_META),
                new SpecCss("font-size", SpecText.Type.CONTAINS, "9"),
                page(new HashMap<String, PageElement>(){{
                    put("object", elementWithCss("font-size", "18px"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should match \"[0-9]+em\""), NULL_META),
                new SpecCss("font-size", SpecText.Type.MATCHES, "[0-9]+em"),
                page(new HashMap<String, PageElement>(){{
                    put("object", elementWithCss("font-size", "18px"));
            }})}
        };
    }

    private PageElement elementWithCss(String cssPropertyName, String value) {
        return new MockedPageElement(10,10,10,10).withCssProperty(cssPropertyName, value);
    }
}
