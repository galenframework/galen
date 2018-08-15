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

import com.galenframework.components.validation.MockedPage;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.SpecContains;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class ContainsValidationTest extends ValidationTestBase {
    private static final boolean CONTAINS_FULLY = false;
    private static final boolean CONTAINS_PARTLY = true;

    @Test
    public void spec_contains_should_allow_to_use_object_groups() {
        PageSpec pageSpec = new PageSpec();
        pageSpec.setObjects(new HashMap<String, Locator>(){{
            put("menu", new Locator("css", "#menu"));
            put("menu_item-1", new Locator("css", "#menu li", 1));
            put("menu_item-2", new Locator("css", "#menu li", 2));
            put("menu_item-3", new Locator("css", "#menu li", 3));
        }});
        pageSpec.setObjectGroups(new HashMap<String, List<String>>() {{
            put("menu_items", asList("menu_item-1", "menu_item-2", "menu_item-3"));
        }});

        Page page = new MockedPage(new HashMap<String, PageElement>(){{
            put("menu", element(0, 0, 1000, 50));
            put("menu_item-1", element(0, 0, 100, 50));
            put("menu_item-2", element(0, 0, 100, 50));
            put("menu_item-3", element(0, 100, 100, 50));
        }});


        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("menu", new SpecContains(asList("&menu_items"), false)).getError();

        assertThat(error, is(notNullValue()));
        assertThat(error, is(new ValidationError(asList("\"menu_item-3\" is outside \"menu\""))));

    }

    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            {specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 100, 100));
                put("menu", element(11, 11, 10, 10));
                put("button", element(60, 50, 40, 40));
            }})},
            {specContains(CONTAINS_PARTLY, "menu", "button"),  page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 100, 100));
                put("menu", element(50, 50, 300, 10));
                put("button", element(10, 10, 100, 40));
            }})},
            {specContains(CONTAINS_PARTLY, "menu", "button"),  page(new HashMap<String, PageElement>(){{
                put("object", element(70, 70, 100, 100));
                put("menu", element(0, 0, 100, 72));
                put("button", element(5, 5, 100, 70));
            }})},
            {specContains(CONTAINS_FULLY, "menu-item-*", "button"),  page(new HashMap<String, PageElement>(){{
                put("object", element(0, 0, 200, 100));
                put("menu-item-1", element(10, 10, 10, 10));
                put("menu-item-2", element(30, 10, 10, 10));
                put("menu-item-3", element(50, 10, 10, 10));
                put("button", element(70, 10, 10, 10));
            }})}
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            {validationResult(areas(new ValidationObject(new Rect(10, 10, 100, 100), "object"), new ValidationObject(new Rect(9, 11, 10, 10), "menu"), new ValidationObject(new Rect(60, 50, 40, 40), "button")),
                    messages("\"menu\" is outside \"object\""), NULL_META),
                specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 100, 100));
                    put("menu", element(9, 11, 10, 10));
                    put("button", element(60, 50, 40, 40));
            }})},

            {validationResult(areas( new ValidationObject(new Rect(10, 10, 100, 100), "object"), new ValidationObject(new Rect(50, 50, 110, 10), "menu"), new ValidationObject(new Rect(10, 10, 101, 40), "button")),
                    messages("\"menu\" is outside \"object\"", "\"button\" is outside \"object\""), NULL_META),
                specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 100, 100));
                    put("menu", element(50, 50, 110, 10));
                    put("button", element(10, 10, 101, 40));
            }})},

            {validationResult(NO_AREA, messages("\"menu\" is not visible on page"), NULL_META),
                specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 100, 100));
                    put("menu", invisibleElement(11, 11, 10, 10));
                    put("button", element(60, 50, 40, 40));
            }})},

            {validationResult(NO_AREA, messages("\"menu\" is absent on page"), NULL_META),
                specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 100, 100));
                    put("menu", absentElement(11, 11, 10, 10));
                    put("button", element(60, 50, 40, 40));
            }})},

            {validationResult(areas(
                    new ValidationObject(new Rect(0, 0, 200, 100), "object"),
                    new ValidationObject(new Rect(10, 10, 10, 10), "menu-item-1"),
                    new ValidationObject(new Rect(30, 10, 10, 10), "menu-item-2"),
                    new ValidationObject(new Rect(350, 10, 10, 10), "menu-item-3"),
                    new ValidationObject(new Rect(70, 10, 10, 10), "button")
                    ),
                    messages("\"menu-item-3\" is outside \"object\""), NULL_META),
                specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(0, 0, 200, 100));
                    put("menu-item-1", element(10, 10, 10, 10));
                    put("menu-item-2", element(30, 10, 10, 10));
                    put("menu-item-3", element(350, 10, 10, 10));
                    put("button", element(70, 10, 10, 10));
            }})},

            {validationResult(NO_AREA, messages("There are no objects matching: menu-item-*"), NULL_META),
                specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                    put("object", element(0, 0, 200, 100));
                    put("button", element(70, 10, 10, 10));
            }})}
        };
    }

    private SpecContains specContains(boolean isPartly, String...objects) {
        return new SpecContains(asList(objects), isPartly);
    }
}
