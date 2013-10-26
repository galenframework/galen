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
import java.util.List;

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
import net.mindengine.galen.specs.SpecAbove;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecBelow;
import net.mindengine.galen.specs.SpecCentered;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecOn;
import net.mindengine.galen.specs.SpecText;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecVisible;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ValidationTest {

    private static final boolean CONTAINS_FULLY = false;
    private static final boolean CONTAINS_PARTLY = true;
    private static final List<ErrorArea> NO_AREA = null;

    
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
          // Contains 
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
          row(specContains(CONTAINS_FULLY, "menu-item-*", "button"),  page(new HashMap<String, PageElement>(){{
              put("object", element(0, 0, 200, 100));
              put("menu-item-1", element(10, 10, 10, 10));
              put("menu-item-2", element(30, 10, 10, 10));
              put("menu-item-3", element(50, 10, 10, 10));
              put("button", element(70, 10, 10, 10));
          }})),
          
          
          // Absent 
          
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("object", invisibleElement(10, 10, 100, 100));
          }})),
          row(specAbsent(), page(new HashMap<String, PageElement>(){{
              put("object", absentElement(10, 10, 100, 100));
          }})),
          
          
          // Visible
          
          row(specVisible(), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
          }})),
          row(specVisible(), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
          }})),
          
          // Inside 
          
          row(specInside("container", location(exact(10), RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 20, 100, 100));
              put("container", element(10, 10, 110, 110));
          }})),
          
          row(specInsidePartly("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(20, 20, 200, 100));
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
          
          row(specInside("container", location(exact(20).withPercentOf("container/height"), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 100, 20));
              put("container", element(5, 5, 120, 50));
          }})),
          row(specInside("container", location(between(15, 22).withPercentOf("container/height"), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 100, 20));
              put("container", element(5, 5, 120, 50));
          }})),
          
          
          // Near 
          
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
          row(specNear("button", location(exact(100).withPercentOf("button/width"), RIGHT)), page(new HashMap<String, PageElement>(){{
              put("object", element(200, 140, 100, 50));
              put("button", element(100, 100, 50, 50));
          }})),
          row(specNear("button", location(between(95, 105).withPercentOf("button/width"), RIGHT)), page(new HashMap<String, PageElement>(){{
              put("object", element(200, 140, 100, 50));
              put("button", element(100, 100, 50, 50));
          }})),
          
          
          // Width 
          
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
          row(specWidth(Range.exact(50).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 15, 50));
              put("container", element(305, 400, 30, 50));
          }})),
          row(specWidth(Range.between(45, 55).withPercentOf("main-container-1/width")), page(new HashMap<String, PageElement>(){{
              put("object", element(305, 140, 15, 50));
              put("main-container-1", element(305, 400, 30, 50));
          }})),
          

          
          // Height 
          
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
          row(specHeight(Range.exact(50).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
              put("object", element(100, 140, 65, 20));
              put("container", element(305, 140, 65, 40));
          }})),
          
          
          // Horizontally 
          
          row(specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(20, 10, 10, 10));
              put("item2",  element(30, 10, 10, 10));
          }})),
          row(specHorizontally(Alignment.CENTERED, "item-*"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item-1",  element(20, 10, 10, 10));
              put("item-2",  element(30, 10, 10, 10));
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
          row(specHorizontally(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 20));
              put("item1",  element(20, 10, 10, 20));
              put("item2",  element(30, 10, 10, 20));
          }})),
          

          // Vertically 
          
          row(specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(10, 20, 10, 10));
              put("item2",  element(10, 30, 10, 10));
          }})),
          row(specVertically(Alignment.CENTERED, "item-*"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item-1",  element(10, 20, 10, 10));
              put("item-2",  element(10, 30, 10, 10));
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
          row(specVertically(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item1",  element(10, 20, 10, 10));
              put("item2",  element(10, 30, 10, 10));
          }})),
          
          
          // Text validation 
          
          row(specTextIs("Some text"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(specTextIs(""), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText(""));
          }})),
          
          row(specTextContains("good"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some good text"));
          }})),
          
          row(specTextStarts("Some"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(specTextEnds("text"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(specTextMatches("Some text with [0-9]+ numbers"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some text with 12412512512521 numbers"));
          }})),
          
          
          // Above 
          
          row(specAbove("button", Range.exact(20)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("button",  element(10, 40, 10, 10));
          }})),
          
          row(specAbove("button", Range.between(20, 25)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("button",  element(10, 42, 10, 10));
          }})),
          
          
          // Below 
          
          row(specBelow("button", Range.exact(20)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 40, 10, 10));
              put("button",  element(10, 10, 10, 10));
          }})),
          
          row(specBelow("button", Range.between(20, 25)), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 42, 10, 10));
              put("button",  element(10, 10, 10, 10));
          }})),
          
          
          // Centered Inside 
          
          row(specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 80, 80));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 81, 81));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(9, 9, 80, 80));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 80, 20));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 30), page(new HashMap<String, PageElement>(){{
              put("object", element(60, 10, 50, 20));
              put("container",  element(0, 0, 200, 200));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 30), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 80, 20));
              put("container",  element(0, 0, 100, 200));
          }})),
          
          row(specCenteredInside("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 20, 80));
              put("container",  element(0, 0, 100, 100));
          }})),
          
          
          // Centered on 
          
          row(specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(80, 80, 90, 90));
              put("button",  element(100, 100, 50, 50));
          }})),
          row(specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(81, 81, 90, 90));
              put("button",  element(100, 100, 50, 50));
          }})),
          row(specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(80, 80, 89, 91));
              put("button",  element(100, 100, 50, 50));
          }})),
          row(specCenteredOn("button", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
              put("object", element(80, 80, 90, 90));
              put("button",  element(100, 100, 50, 50));
          }})),
          row(specCenteredOn("button", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
              put("object", element(80, 80, 10, 90));
              put("button",  element(100, 100, 50, 50));
          }})),
          row(specCenteredOn("button", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
              put("object", element(80, 80, 90, 10));
              put("button",  element(100, 100, 50, 50));
          }})),
          
          
          
          // On
          
          row(specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(90, 110, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn("container", location(exact(10), RIGHT), location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(110, 90, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn("container", location(exact(90), RIGHT), location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(190, 110, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn("container", location(exact(90), RIGHT), location(exact(20), BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(190, 120, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
        };
    }


    
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
          // Contains 
                
          row(new ValidationError(areas(new ErrorArea(new Rect(9, 11, 10, 10), "menu"), new ErrorArea(new Rect(10, 10, 100, 100), "object")), messages("\"menu\" is outside \"object\"")),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(9, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(50, 50, 110, 10), "menu"), new ErrorArea(new Rect(10, 10, 101, 40), "button"), new ErrorArea(new Rect(10, 10, 100, 100), "object")), messages("\"menu\" is outside \"object\"", "\"button\" is outside \"object\"")),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(50, 50, 110, 10));
                  put("button", element(10, 10, 101, 40));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"menu\" is not visible on page")),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", invisibleElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"menu\" is absent on page")),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", absentElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(350, 10, 10, 10), "menu-item-3"), new ErrorArea(new Rect(0, 0, 200, 100), "object")), messages("\"menu-item-3\" is outside \"object\"")),
                  specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 0, 200, 100));
                      put("menu-item-1", element(10, 10, 10, 10));
                      put("menu-item-2", element(30, 10, 10, 10));
                      put("menu-item-3", element(350, 10, 10, 10));
                      put("button", element(70, 10, 10, 10));
          }})),
          row(new ValidationError(NO_AREA, messages("There are no objects matching: menu-item-*")),
                  specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 0, 200, 100));
                      put("button", element(70, 10, 10, 10));
          }})),
          
          // Absent 
          
          row(new ValidationError(singleArea(new Rect(10, 10, 100, 100), "object"), messages("\"object\" is not absent on page")),
              specAbsent(), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
          }})),
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
              specAbsent(), page(new HashMap<String, PageElement>(){{
                  put("blabla", absentElement(10, 10, 100, 100));
          }})),
          
          // Visible 
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
              specVisible(), page(new HashMap<String, PageElement>(){{
                  put("object", invisibleElement(10, 10, 100, 100));
          }})),
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
              specVisible(), page(new HashMap<String, PageElement>(){{
                  put("blabla", absentElement(10, 10, 100, 100));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specVisible(), page(new HashMap<String, PageElement>(){{
                  put("object", absentElement(10, 10, 100, 100));
              }})),
          
          
          // Inside
          
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 10, 500, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is not completely inside")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 500, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 10, 500, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is not completely inside")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 500, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(190, 110, 500, 500), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is 180px left instead of 10px")),
              specInsidePartly("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(190, 110, 500, 500));
                  put("container", element(10, 10, 100, 100));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 10, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is 30px left instead of 10px")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 20, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is 30px left and 20px top instead of 10px")),
                  specInside("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 20, 50, 50));
                      put("container", element(0, 0, 130, 120));
              }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 10, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is 50px right instead of 10px")),
              specInside("container", location(exact(10), RIGHT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 20, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is 20px top instead of 10px")),
              specInside("container", location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 20, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 10, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), messages("\"object\" is 60px bottom instead of 10px")),
              specInside("container", location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 10, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), messages("\"object\" is 30px left which is not in range of 10 to 20px")),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"container\" in page spec")),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 5, 50, 50), "object"), new ErrorArea(new Rect(0, 0, 130, 120), "container")), 
                  messages("\"object\" is 30px left instead of 10px, is 5px top instead of 20px")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 5, 10, 50), "object"), new ErrorArea(new Rect(0, 0, 50, 120), "container")), messages("\"object\" is 30px left instead of 10px")),
                  specInside("container", location(exact(20).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 5, 10, 50));
                      put("container", element(0, 0, 50, 120));
          }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(30, 5, 10, 50), "object"), new ErrorArea(new Rect(0, 0, 50, 120), "container")), messages("\"object\" is 30px left which is not in range of 10 to 20px")),
                  specInside("container", location(between(20, 40).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 5, 10, 50));
                      put("container", element(0, 0, 50, 120));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", absentElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", invisibleElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"container\" is absent on page")), 
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", absentElement(0, 0, 130, 120));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"container\" is not visible on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", invisibleElement(0, 0, 130, 120));
          }})),
          
          
          // Near 
          row(new ValidationError(areas(new ErrorArea(new Rect(90, 5, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 100, 50), "button")), 
                  messages("\"object\" is 10px left instead of 30px")),
                  specNear("button", location(exact(30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(90, 5, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 100, 50), "button")), 
                  messages("\"object\" is 10px left which is not in range of 20 to 30px")),
                  specNear("button", location(between(20, 30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
              
          row(new ValidationError(areas(new ErrorArea(new Rect(90, 130, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 100, 50), "button")), 
                  messages("\"object\" is 10px left and 20px top instead of 30px")),
                  specNear("button", location(exact(30), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(310, 250, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 100, 50), "button")), 
                  messages("\"object\" is 10px right instead of 30px, is 0px bottom which is not in range of 10 to 20px")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          
          row(new ValidationError(areas(new ErrorArea(new Rect(90, 130, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 50, 50), "button")),
                  messages("\"object\" is 10px left instead of 20px")),
                  specNear("button", location(exact(40).withPercentOf("button/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 50, 50));
          }})),
          
          row(new ValidationError(areas(new ErrorArea(new Rect(90, 130, 100, 50), "object"), new ErrorArea(new Rect(200, 200, 50, 50), "button")), 
                  messages("\"object\" is 10px left which is not in range of 20 to 25px")),
                  specNear("button", location(between(40, 50).withPercentOf("button/area/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 50, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"button\" is absent on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"button\" is not visible on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", invisibleElement(200, 200, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"button\" in page spec")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          
          // Width 
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px instead of 10px")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be greater than 110px")),
                  specWidth(Range.greaterThan(110.0)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be less than 90px")),
                  specWidth(Range.lessThan(90.0)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px which is not in range of 10 to 40px")),
                  specWidth(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px instead of 20px")),
                  specWidth(exact(10).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 200, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px which is not in range of 20 to 40px")),
                  specWidth(between(10, 20).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 200, 50));
          }})),
          
          
          // Height 
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px instead of 10px")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px which is not in range of 10 to 40px")),
                  specHeight(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px instead of 20px")),
                  specHeight(exact(10).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 100, 200));
          }})),
          
          row(new ValidationError(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px which is not in range of 20 to 30px")),
                  specHeight(between(10, 15).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 100, 200));
          }})),
          
          // Horizontally 
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"item1\" in page spec")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, messages("\"item1\" is not visible on page")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", invisibleElement(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("\"item2\" is absent on page")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", absentElement(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("item1", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 15), "item2"), messages("\"item2\" is not aligned horizontally centered with \"object\"")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 5, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 15), "item-2"), messages("\"item-2\" is not aligned horizontally centered with \"object\"")),
                  specHorizontally(Alignment.CENTERED, "item-*"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item-1", element(10, 5, 10, 20));
                      put("item-2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 10, 10, 20), "item1"), new ErrorArea(new Rect(10, 10, 50, 20), "item2")), messages("\"item1\", \"item2\" are not aligned horizontally centered with \"object\"")),
                  specHorizontally(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 50, 20));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 20), "item1"), messages("\"item1\" is not aligned horizontally top with \"object\"")),
                  specHorizontally(Alignment.TOP, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 15, 10, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 15, 10, 15));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 5), "item2"), messages("\"item2\" is not aligned horizontally bottom with \"object\"")),
                  specHorizontally(Alignment.BOTTOM, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(10, 5, 10, 15));
                      put("item2", element(10, 10, 10, 5));
          }})),
          row(new ValidationError(singleArea(new Rect(30, 10, 10, 5), "item2"), messages("\"item2\" is not aligned horizontally all with \"object\"")),
                  specHorizontally(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(20, 10, 10, 10));
                      put("item2", element(30, 10, 10, 5));
          }})),
          row(new ValidationError(singleArea(new Rect(30, 10, 15, 5), "item2"), messages("\"item2\" is not aligned horizontally all with \"object\"")),
                  specHorizontally(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(20, 10, 10, 10));
                      put("item2", element(30, 10, 15, 5));
          }})),
          
          
          // Vertically 
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"item1\" in page spec")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, messages("\"item1\" is not visible on page")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", invisibleElement(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("\"item2\" is absent on page")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", absentElement(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("item1", element(10, 10, 50, 10));
                      put("item2", element(10, 10, 10, 15));                      
          }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item1", element(10, 10, 10, 20));
                      put("item2", element(10, 10, 10, 15));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 20, 10, 10), "item1"), messages("\"item1\" is not aligned vertically centered with \"object\"")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(15, 30, 10, 10));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 20, 10, 10), "item-1"), messages("\"item-1\" is not aligned vertically centered with \"object\"")),
                  specVertically(Alignment.CENTERED, "item-*"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item-1", element(10, 20, 10, 10));
                      put("item-2", element(15, 30, 10, 10));
          }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 20, 10, 10), "item1"), new ErrorArea(new Rect(10, 30, 10, 10), "item2")), messages("\"item1\", \"item2\" are not aligned vertically centered with \"object\"")),
                  specVertically(Alignment.CENTERED, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          row(new ValidationError(singleArea(new Rect(5, 20, 10, 10), "item1"), messages("\"item1\" is not aligned vertically left with \"object\"")),
                  specVertically(Alignment.LEFT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(5, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 30, 10, 10), "item2"), messages("\"item2\" is not aligned vertically right with \"object\"")),
                  specVertically(Alignment.RIGHT, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item1", element(20, 20, 10, 10));
                      put("item2", element(10, 30, 10, 10));
          }})),
          row(new ValidationError(singleArea(new Rect(10, 30, 5, 10), "item2"), messages("\"item2\" is not aligned vertically all with \"object\"")),
                  specVertically(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(10, 30, 5, 10));
          }})),
          row(new ValidationError(singleArea(new Rect(15, 30, 5, 10), "item2"), messages("\"item2\" is not aligned vertically all with \"object\"")),
                  specVertically(Alignment.ALL, "item1", "item2"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item1", element(10, 20, 10, 10));
                      put("item2", element(15, 30,   5, 10));
          }})),
          
          
          // Text validation 
          
          row(new ValidationError(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>())),
                  
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 10, 10));
          }})),
          
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 10, 10));
          }})),
          
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should be \"some wrong text\"")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should contain \"good\"")),
                  specTextContains("good"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should start with \"text\"")),
                  specTextStarts("text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should end with \"Some\"")),
                  specTextEnds("Some"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(new ValidationError(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should match \"Some [0-9]+ text\"")),
                  specTextMatches("Some [0-9]+ text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          
          // Above 
          
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),    
          row(new ValidationError(NO_AREA, messages("\"button\" is not visible on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", invisibleElement(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"button\" is absent on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", absentElement(10, 60, 10, 10));
              }})),    
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 40, 10, 10), "object"), new ErrorArea(new Rect(10, 60, 10, 10), "button")), 
                  messages("\"object\" is 10px above \"button\" instead of 20px")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 40, 10, 10), "object"), new ErrorArea(new Rect(10, 60, 10, 10), "button")), 
                  messages("\"object\" is 10px above \"button\" which is not in range of 20 to 30px")),
                  specAbove("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
              
              
          // Below 
              
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),    
          row(new ValidationError(NO_AREA, messages("\"button\" is not visible on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", invisibleElement(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"button\" is absent on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", absentElement(10, 60, 10, 10));
              }})),    
          row(new ValidationError(areas(new ErrorArea(new Rect(10, 60, 10, 10), "object"), new ErrorArea(new Rect(10, 40, 10, 10), "button")), 
                  messages("\"object\" is 10px below \"button\" instead of 20px")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 60, 10, 10));
                      put("button", element(10, 40, 10, 10));
              }})),
              row(new ValidationError(areas(new ErrorArea(new Rect(10, 60, 10, 10), "object"), new ErrorArea(new Rect(10, 40, 10, 10), "button")),
                  messages("\"object\" is 10px below \"button\" which is not in range of 20 to 30px")),
                  specBelow("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 60, 10, 10));
                      put("button", element(10, 40, 10, 10));
              }})),
              
      
          // Centered
      
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("container", element(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("container", element(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"container\" is absent on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("container", absentElement(10, 60, 10, 10));
              }})),
          row(new ValidationError(NO_AREA, messages("\"container\" is not visible on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("container", invisibleElement(10, 60, 10, 10));
              }})),
                      
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 20, 80, 60), "object"), new ErrorArea(new Rect(0, 0, 100, 100), "container")), 
                  messages("\"object\" is not centered horizontally inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 20, 80, 60));
                      put("container", element(0, 0, 100, 100));
              }})),
              
              
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 20, 75, 60), "object"), new ErrorArea(new Rect(0, 0, 100, 100), "container")), 
                  messages("\"object\" is not centered horizontally inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 10), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 20, 75, 60));
                      put("container", element(0, 0, 100, 100));
              }})),    
        
          row(new ValidationError(areas(new ErrorArea(new Rect(0, 20, 120, 60), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is not centered horizontally inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 20, 120, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 10, 100, 60), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is not centered vertically inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 100, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 10, 10, 60), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is not centered horizontally inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 10, 10, 60), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is not centered vertically on \"container\"")),
                  specCenteredOn("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(20, 10, 10, 60), "object"), new ErrorArea(new Rect(10, 10, 100, 100), "container")), 
                  messages("\"object\" is not centered horizontally on \"container\"")),
                  specCenteredOn("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          
                  
           // On
          row(new ValidationError(NO_AREA, messages("\"object\" is not visible on page")),
                  specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(new ValidationError(NO_AREA, messages("\"object\" is absent on page")),
                  specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(new ValidationError(NO_AREA, messages("\"container\" is not visible on page")),
                  specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 50, 50));
                      put("container", invisibleElement(100, 100, 100, 100));
              }})),
          row(new ValidationError(NO_AREA, messages("\"container\" is absent on page")),
                  specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 50, 50));
                      put("container", absentElement(100, 100, 100, 100));
              }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(95, 110, 50, 50), "object"), new ErrorArea(new Rect(100, 100, 100, 100), "container")), 
                  messages("\"object\" is 5px left instead of 10px")),
                  specOn("container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(95, 110, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(new ValidationError(areas(new ErrorArea(new Rect(105, 90, 50, 50), "object"), new ErrorArea(new Rect(100, 100, 100, 100), "container")), 
                  messages("\"object\" is 5px right which is not in range of 10 to 15px, is 10px top instead of 5px")),
                  specOn("container", location(between(10, 15), RIGHT), location(exact(5), TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(105, 90, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
        };
    }
    
    
    private List<ErrorArea> areas(ErrorArea...errorAreas) {
        return Arrays.asList(errorAreas);
    }

    private List<String> messages(String...messages) {
        return Arrays.asList(messages);
    }

    private List<ErrorArea> singleArea(Rect rect, String tooltip) {
        return Arrays.asList(new ErrorArea(rect, tooltip));
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

    private SpecInside specInsidePartly(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, Arrays.asList(locations)).withPartlyCheck();
    }

    
    private SpecOn specOn(String parentObjectName, Location...locations) {
        return new SpecOn(parentObjectName, Arrays.asList(locations));
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
    
    private SpecVisible specVisible() {
        return new SpecVisible();
    }

    private SpecAbove specAbove(String object, Range range) {
		return new SpecAbove(object, range);
	}
    
    private SpecBelow specBelow(String object, Range range) {
		return new SpecBelow(object, range);
	}
    
    private SpecCentered specCenteredOn(String object, SpecCentered.Alignment alignment) {
        return new SpecCentered(object, alignment, SpecCentered.Location.ON);
    }

    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE);
    }
    
    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment, int errorRate) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE).withErrorRate(errorRate);
    }


    public Object[] row (Object...args) {
        return args;
    }
}
