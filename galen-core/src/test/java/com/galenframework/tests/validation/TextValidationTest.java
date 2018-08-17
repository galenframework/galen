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
import com.galenframework.specs.SpecText;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static java.util.Arrays.asList;

public class TextValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            {specTextIs("Some text"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {specTextIs("some text").withOperations(asList("lowercase")), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some teXt"));
            }})},

            {specTextIs("SOME TEXT").withOperations(asList("uppercase")), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some Text"));
            }})},

            {specTextIs(""), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText(""));
            }})},

            {specTextContains("good"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some good text"));
            }})},

            {specTextStarts("Some"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {specTextEnds("text"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {specTextMatches("Some text with [0-9]+ numbers"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some text with 12412512512521 numbers"));
            }})},

            {specTextMatches(".* some.* multiline"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("A text with some \n more multiline"));
            }})}

        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specTextIs("some wrong text"),
                page(new HashMap<>())
            },

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specTextIs("some wrong text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specTextIs("some wrong text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 10, 10));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should be \"some wrong text\""), NULL_META),
                specTextIs("some wrong text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should contain \"good\""), NULL_META),
                specTextContains("good"),
                page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should start with \"text\""), NULL_META),
                specTextStarts("text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should end with \"Some\""), NULL_META),
                specTextEnds("Some"),
                page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10).withText("Some text"));
            }})},

            {validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should match \"Some [0-9]+ text\""), NULL_META),
                specTextMatches("Some [0-9]+ text"),
                page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 10, 10).withText("Some text"));
            }})}
        };
    }

    private SpecText specTextIs(String text) {
        return new SpecText(SpecText.Type.IS, text);
    }

    private SpecText specTextContains(String text) {
        return new SpecText(SpecText.Type.CONTAINS, text);
    }

    private SpecText specTextStarts(String text) {
        return new SpecText(SpecText.Type.STARTS, text);
    }

    private SpecText specTextEnds(String text) {
        return new SpecText(SpecText.Type.ENDS, text);
    }

    private SpecText specTextMatches(String text) {
        return new SpecText(SpecText.Type.MATCHES, text);
    }
}
