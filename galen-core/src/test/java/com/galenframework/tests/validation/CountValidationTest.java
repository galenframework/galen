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

import static com.galenframework.specs.Range.*;
import java.util.HashMap;
import com.galenframework.specs.*;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationResult;
import org.testng.annotations.DataProvider;

public class CountValidationTest extends ValidationTestBase {
    private static final Spec NO_SPEC = null;

    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][] {
            {new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
            }})},

            {new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", lessThan(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
            }})},

            {new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", greaterThan(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("menu-item-4", element(0,0, 10,10));
            }})},

            {new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", between(3, 5)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("menu-item-4", element(0,0, 10,10));
            }})},

            {new SpecCount(SpecCount.FetchType.ANY, "menu-item-*, box-*", exact(4)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("box-123", element(0,0, 10,10));
            }})},

            {new SpecCount(SpecCount.FetchType.VISIBLE, "menu-item-*", exact(1)), page(new HashMap<String, PageElement>() {{
                put("object", element(0, 0, 10, 10));
                put("menu-item-1", invisibleElement(0, 0, 10, 10));
                put("menu-item-2", element(0, 0, 10, 10));
                put("menu-item-3", invisibleElement(0, 0, 10, 10));
                put("menu-item-4", invisibleElement(0, 0, 10, 10));
            }})},

            {new SpecCount(SpecCount.FetchType.ABSENT, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", absentElement(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", absentElement(0,0, 10,10));
                put("menu-item-4", absentElement(0,0, 10,10));
            }})},
        };
    }

    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                new ValidationError(messages("There are 3 objects matching \"menu-item-*\" instead of 2"))),
                new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", exact(2)), page(new HashMap<String, PageElement>() {{
                put("object", element(100, 90, 100, 40));
                put("menu-item-1", element(100, 90, 100, 40));
                put("menu-item-2", element(100, 90, 100, 40));
                put("menu-item-3", element(100, 90, 100, 40));
            }})},

            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                new ValidationError(messages("There are 2 visible objects matching \"menu-item-*\" instead of 3"))),
                new SpecCount(SpecCount.FetchType.VISIBLE, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>() {{
                put("object", element(100, 90, 100, 40));
                put("menu-item-1", element(100, 90, 100, 40));
                put("menu-item-2", element(100, 90, 100, 40));
                put("menu-item-3", absentElement(100, 90, 100, 40));
            }})},

            {new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                new ValidationError(messages("There are 1 absent objects matching \"menu-item-*\" instead of 3"))),
                new SpecCount(SpecCount.FetchType.ABSENT, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>() {{
                put("object", element(100, 90, 100, 40));
                put("menu-item-1", element(100, 90, 100, 40));
                put("menu-item-2", element(100, 90, 100, 40));
                put("menu-item-3", absentElement(100, 90, 100, 40));
            }})}
        };
    }
}
