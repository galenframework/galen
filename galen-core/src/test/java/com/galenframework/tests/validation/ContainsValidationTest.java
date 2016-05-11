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

import com.galenframework.components.validation.MockedPage;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.specs.SpecContains;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class ContainsValidationTest {

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

    private MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }
}
