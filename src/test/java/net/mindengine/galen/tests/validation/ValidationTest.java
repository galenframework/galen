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

import static net.mindengine.galen.specs.Range.between;
import static net.mindengine.galen.specs.Range.exact;
import static net.mindengine.galen.specs.Side.BOTTOM;
import static net.mindengine.galen.specs.Side.LEFT;
import static net.mindengine.galen.specs.Side.RIGHT;
import static net.mindengine.galen.specs.Side.TOP;
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
import net.mindengine.galen.specs.Alignment;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecWidth;
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
    public void shouldGiveError(ValidationError expectedError, Spec spec, MockedPage page) {
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
          
          
          /* Inside */
          
          row(specInside("container", location(exact(10), RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 20, 100, 100));
              put("container", element(10, 10, 110, 110));
          }})),
          
          row(specInside("container", location(between(5, 12), RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 20, 100, 100));
              put("container", element(10, 10, 110, 110));
          }})),
          
          row(specInside("container", location(between(5, 20), LEFT, RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("container", element(5, 5, 120, 120));
          }})),
          
          row(specInside("container", location(exact(5), LEFT), location(between(5, 15), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 100, 100));
              put("container", element(5, 5, 120, 120));
          }})),
          
          
          /* Near */
          
          row(specNear("button", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(90, 140, 100, 50));
              put("button", element(200, 200, 100, 50));
          }})),
          
          row(specNear("button", location(between(5, 12), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(90, 140, 100, 50));
              put("button", element(200, 200, 100, 50));
          }})),
          
          row(specNear("button", location(between(5, 20), RIGHT, BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(310, 260, 100, 50));
              put("button", element(200, 200, 100, 50));
          }})),
          
          row(specNear("button", location(exact(5), RIGHT), location(between(5, 15), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 100, 50));
              put("button", element(200, 200, 100, 50));
          }})),
          
          
          /* Width */
          
          row(specWidth(Range.exact(20)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 20, 50));
          }})),
          row(specWidth(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 20, 50));
          }})),
          row(specWidth(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 30, 50));
          }})),
          row(specWidth(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 25, 50));
          }})),

          
          /* Height */
          
          row(specHeight(Range.exact(20)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 60, 20));
          }})),
          row(specHeight(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 60, 20));
          }})),
          row(specHeight(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 60, 30));
          }})),
          row(specHeight(Range.between(20, 30)), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 65, 25));
          }})),
          
          
          
          /* Horizontally */
          
          row(specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(20, 10, 10, 10));
              put("item2",  element(30, 10, 10, 10));
          }})),
          row(specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 10, 10));
              put("item1",  element(20, 10, 10, 20));
              put("item2",  element(30, 15, 10, 10));
          }})),
          row(specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 10, 10));
              put("item1",  element(20, 10, 10, 19));
              put("item2",  element(30, 15, 10, 10));
          }})),
          row(specHorizontally(Alignment.TOP, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(20, 10, 10, 20));
              put("item2",  element(30, 10, 10, 40));
          }})),
          row(specHorizontally(Alignment.BOTTOM, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 40, 10, 10));
              put("item1",  element(20, 30, 10, 20));
              put("item2",  element(30, 10, 10, 40));
          }})),
          
          /* Vertically */
          row(specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(10, 20, 10, 10));
              put("item2",  element(10, 30, 10, 10));
          }})),
          row(specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(15, 10, 10, 10));
              put("item1",  element(10, 20, 20, 10));
              put("item2",  element(15, 30, 10, 10));
          }})),
          row(specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(15, 10, 10, 10));
              put("item1",  element(10, 20, 19, 10));
              put("item2",  element(15, 30, 10, 10));
          }})),
          row(specVertically(Alignment.LEFT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(10, 20, 20, 10));
              put("item2",  element(10, 30, 30, 10));
          }})),
          row(specVertically(Alignment.RIGHT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(30, 10, 10, 10));
              put("item1",  element(20, 20, 20, 10));
              put("item2",  element(10, 30, 30, 10));
          }})),
          
          
        };
    }
    
    
    
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
          /* Contains */
          row(new ValidationError(new Rect(10, 10, 100, 100), "\"menu\" is outside \"object\""),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(9, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          row(new ValidationError(new Rect(10, 10, 100, 100), "\"menu\" is outside \"object\"", "\"button\" is outside \"object\""),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(50, 50, 110, 10));
                  put("button", element(10, 10, 101, 40));
          }})),
          
          row(new ValidationError(new Rect(10, 10, 100, 100), "\"menu\" is not visible on page"),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", invisibleElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          row(new ValidationError(new Rect(10, 10, 100, 100), "\"menu\" is absent on page"),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", absentElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          
          /* Absent */
          
          row(new ValidationError(new Rect(10, 10, 100, 100), "\"object\" is not absent on page"),
              specAbsent(), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
          }})),
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
              specAbsent(), page(new HashMap<String, PageElement>(){{
                  put("blabla", absentElement(10, 10, 100, 100));
          }})),
          
          /* Inside with one location*/
          
          row(new ValidationError(new Rect(30, 10, 50, 50), "\"object\" is 30px left instead of 10px"),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(new Rect(30, 20, 50, 50), "\"object\" is 30px left and 20px top instead of 10px"),
                  specInside("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 20, 50, 50));
                      put("container", element(0, 0, 130, 120));
              }})),
          
          row(new ValidationError(new Rect(30, 10, 50, 50), "\"object\" is 50px right instead of 10px"),
              specInside("container", location(exact(10), RIGHT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(new Rect(30, 20, 50, 50), "\"object\" is 20px top instead of 10px"),
              specInside("container", location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 20, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(new Rect(30, 10, 50, 50), "\"object\" is 60px bottom instead of 10px"),
              specInside("container", location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(new Rect(30, 10, 50, 50), "\"object\" is 30px left which is not in range of 10px to 20px"),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"container\" in page spec"),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
          }})),
          
          row(new ValidationError(new Rect(30, 5, 50, 50), "\"object\" is 30px left instead of 10px, is 5px top instead of 20px"),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", absentElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", invisibleElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, "\"container\" is absent on page"), 
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", absentElement(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, "\"container\" is absent on page"),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", invisibleElement(0, 0, 130, 120));
          }})),
          
          
          /* Near */
          row(new ValidationError(new Rect(90, 5, 100, 50), "\"object\" is 10px left instead of 30px"),
                  specNear("button", location(exact(30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(90, 5, 100, 50), "\"object\" is 10px left which is not in range of 20px to 30px"),
                  specNear("button", location(between(20, 30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
              
          row(new ValidationError(new Rect(90, 130, 100, 50), "\"object\" is 10px left and 20px top instead of 30px"),
                  specNear("button", location(exact(30), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(310, 250, 100, 50), "\"object\" is 10px right instead of 30px, is 0px bottom which is not in range of 10px to 20px"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"button\" is absent on page"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"button\" is absent on page"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", invisibleElement(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"button\" in page spec"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          
          /* Width */
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(100, 100, 100, 50), "\"object\" width is 100px instead of 10px"),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(100, 100, 100, 50), "\"object\" width is 100px which is not in range of 10px to 40px"),
                  specWidth(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          
          /* Height */
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(100, 100, 100, 50), "\"object\" height is 50px instead of 10px"),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(new Rect(100, 100, 100, 50), "\"object\" height is 50px which is not in range of 10px to 40px"),
                  specHeight(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          
          /* Horizontally */
          
          row(new ValidationError(NO_AREA, "Cannot find locator for \"item1\" in page spec"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, "\"item1\" is absent on page"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", invisibleElement(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "\"item2\" is absent on page"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", absentElement(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("item1", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(new Rect(10, 10, 10, 15), "\"item2\" is not aligned horizontally centered with \"object\""),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 5, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(new Rect(10, 10, 50, 20), "\"item1\", \"item2\" are not aligned horizontally centered with \"object\""),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 50, 20));
          }})),
          row(new ValidationError(new Rect(10, 10, 10, 20), "\"item1\" is not aligned horizontally top with \"object\""),
                  specHorizontally(Alignment.TOP, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 15, 10, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 15, 10, 15));
          }})),
          row(new ValidationError(new Rect(10, 10, 10, 5), "\"item2\" is not aligned horizontally bottom with \"object\""),
                  specHorizontally(Alignment.BOTTOM, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(10, 5, 10, 15));
                      put("item2", element(10, 10, 10, 5));
          }})),
          
          /* Vertically */
          row(new ValidationError(NO_AREA, "Cannot find locator for \"item1\" in page spec"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, "\"item1\" is absent on page"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", invisibleElement(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "\"item2\" is absent on page"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", absentElement(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "Cannot find locator for \"object\" in page spec"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("item1", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, "\"object\" is absent on page"),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(new Rect(10, 20, 10, 10), "\"item1\" is not aligned vertically centered with \"object\""),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(15, 30, 10, 10));
          }})),
          row(new ValidationError(new Rect(10, 20, 10, 20), "\"item1\", \"item2\" are not aligned vertically centered with \"object\""),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          row(new ValidationError(new Rect(5, 20, 10, 10), "\"item1\" is not aligned vertically left with \"object\""),
                  specVertically(Alignment.LEFT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(5, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          row(new ValidationError(new Rect(10, 30, 10, 10), "\"item2\" is not aligned vertically right with \"object\""),
                  specVertically(Alignment.RIGHT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(20, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          
          
          //TODO verify if area of object is returned incorrectly (width < 1 and height < 1)
          
          //TODO refactor areas. Should be list of areas with tooltips instead of just one rect area.
          
          //TODO refactor validations. Should be objects instead of classes
          
        };
    }
    
    private SpecVertically specVertically(Alignment alignment, String...objectNames) {
        return new SpecVertically(alignment, Arrays.asList(objectNames));
    }

    private SpecHorizontally specHorizontally(Alignment alignment, String...objectNames) {
        return new SpecHorizontally(alignment, Arrays.asList(objectNames));
    }
    
    private SpecHeight specHeight(Range range) {
        return new SpecHeight(range);
    }

    private SpecWidth specWidth(Range range) {
        return new SpecWidth(range);
    }

    private SpecNear specNear(String secondObjectName, Location...locations) {
        return new SpecNear(secondObjectName, Arrays.asList(locations));
    }

    private SpecInside specInside(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, Arrays.asList(locations));
    }

    private Location location(Range exact, Side...sides) {
        return new Location(exact, Arrays.asList(sides));
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
