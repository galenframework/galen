/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.HashMap;

import net.mindengine.galen.components.validation.MockedAbsentPageElement;
import net.mindengine.galen.components.validation.MockedInvisiblePageElement;
import net.mindengine.galen.components.validation.MockedPage;
import net.mindengine.galen.components.validation.MockedPageElement;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ValidationTest {

    private static final boolean CONTAINS_FULLY = false;
    private static final boolean CONTAINS_PARTLY = true;
    private static final Rect NO_AREA = null;

    
    @Test(dataProvider="provideGoodSamples")
    public void shouldPassValidation(Spec spec, MockedPage page) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(page, pageSpec);
        ValidationError error = validation.check("object", spec);
        
        assertThat(error, is(nullValue()));
    }
    
    @Test(dataProvider="provideBadSamples")
    public void shouldGiveError(Spec spec, MockedPage page, ValidationError expectedError) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(page, pageSpec);
        ValidationError error = validation.check("object", spec);
        
        assertThat(error, is(notNullValue()));
        assertThat(error, is(expectedError));
    }
    
    
    private PageSpec createMockedPageSpec(MockedPage page) {
        PageSpec pageSpec = new PageSpec();
        
        for (String objectName : page.getElements().keySet()) {
            pageSpec.getObjects().put(objectName, new Locator("id", objectName));
        }
        return pageSpec;
    }

    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][] {
          /* Contains */
          row(specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(11, 11, 10, 10));
              put("button", element(60, 50, 40, 40));
          }})),
          row(specContains(CONTAINS_PARTLY, "menu", "button"),  page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(50, 50, 300, 10));
              put("button", element(10, 10, 100, 40));
          }})),
          row(specContains(CONTAINS_PARTLY, "menu", "button"),  page(new HashMap<String, PageElement>(){{
              put("object", element(70, 70, 100, 100));
              put("menu", element(0, 0, 100, 72));
              put("button", element(5, 5, 100, 70));
          }})),
          
          /* Absent */
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("object", invisibleElement(10, 10, 100, 100));
          }})),
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("object", absentElement(10, 10, 100, 100));
          }})),
          
        };
    }
    
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
          /* Contains */
          row(specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(9, 11, 10, 10));
              put("button", element(60, 50, 40, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"menu\" is outside the specified object \"object\"")),
          
          row(specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(50, 50, 110, 10));
              put("button", element(10, 10, 101, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"menu\" is outside the specified object \"object\"", "Object \"button\" is outside the specified object \"object\"")),
          
          row(specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", invisibleElement(11, 11, 10, 10));
              put("button", element(60, 50, 40, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"menu\" is not visible on page")),
          
          row(specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", absentElement(11, 11, 10, 10));
              put("button", element(60, 50, 40, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"menu\" is absent on page")),
          
          
          /* Absent */
          
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"object\" is not absent on page")),
          
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("blabla", absentElement(10, 10, 100, 100));
          }}), new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec")),
        };
    }
    
    
    

    private MockedPage page(HashMap<String, PageElement> elements) {
        return new MockedPage(elements);
    }

    private MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }
    
    protected PageElement invisibleElement(int left, int top, int width, int height) {
        return new MockedInvisiblePageElement(left, top, width, height);
    }
    
    private MockedPageElement absentElement(int left, int top, int width, int height) {
        return new MockedAbsentPageElement(left, top, width, height);
    }
    
    private SpecContains specContains(boolean isPartly, String...objects) {
        return new SpecContains(Arrays.asList(objects), isPartly);
    }
    
    private SpecAbsent specAbsent() {
        return new SpecAbsent();
    }


    public Object[] row (Object...args) {
        return args;
    }
}
