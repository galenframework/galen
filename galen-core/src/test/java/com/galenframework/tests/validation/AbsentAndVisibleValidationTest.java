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
import com.galenframework.specs.SpecAbsent;
import com.galenframework.specs.SpecVisible;
import org.testng.annotations.DataProvider;

import java.util.HashMap;


public class AbsentAndVisibleValidationTest extends ValidationTestBase {
    @DataProvider
    @Override
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            // Absent

            {specAbsent(), page(new HashMap<String, PageElement>(){{
            }})},

            {specAbsent(), page(new HashMap<String, PageElement>(){{
                put("object", invisibleElement(10, 10, 100, 100));
            }})},

            {specAbsent(), page(new HashMap<String, PageElement>(){{
                put("object", absentElement(10, 10, 100, 100));
            }})},

            // Visible

            {specVisible(), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 100, 100));
            }})},

            {specVisible(), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 100, 100));
            }})},
        };
    }

    @DataProvider
    @Override
    public Object[][] provideBadSamples() {
        return new Object[][]{
            // Absent

            {validationResult(singleArea(new Rect(10, 10, 100, 100), "object"), messages("\"object\" is not absent on page"), NULL_META),
                specAbsent(), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 100, 100));
            }})},

            // Visible

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specVisible(), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 100, 100));
            }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec"), NULL_META),
                specVisible(), page(new HashMap<String, PageElement>(){{
                    put("blabla", absentElement(10, 10, 100, 100));
            }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specVisible(), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 100, 100));
            }})}
        };
    }

    private SpecAbsent specAbsent() {
        return new SpecAbsent();
    }

    private SpecVisible specVisible() {
        return new SpecVisible();
    }
}
