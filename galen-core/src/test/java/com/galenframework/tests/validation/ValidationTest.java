/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
import static java.util.Arrays.asList;
import static com.galenframework.specs.Side.BOTTOM;
import static com.galenframework.specs.Side.LEFT;
import static com.galenframework.specs.Side.RIGHT;
import static com.galenframework.specs.Side.TOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.specs.*;
import com.galenframework.components.validation.MockedAbsentPageElement;
import com.galenframework.components.validation.MockedInvisiblePageElement;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationResult;
import com.galenframework.rainbow4j.Rainbow4J;

import com.galenframework.rainbow4j.filters.BlurFilter;
import com.galenframework.rainbow4j.filters.ImageFilter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ValidationTest {

    private static final boolean CONTAINS_FULLY = false;
    private static final boolean CONTAINS_PARTLY = true;
    private static final List<ValidationObject> NO_AREA = null;
    private static final boolean PIXEL_UNIT = true;
    private static final boolean PERCENTAGE_UNIT = false;
    private static final Spec NO_SPEC = null;


    private BufferedImage testImage = loadTestImage("/color-scheme-image-1.png");
    private BufferedImage imageComparisonTestScreenshot = loadTestImage("/imgs/page-screenshot.png");
    
    @Test(dataProvider="provideGoodSamples")
    public void shouldPassValidation(Spec spec, MockedPage page) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("object", spec).getError();
        
        assertThat(error, is(nullValue()));
    }
    
    private BufferedImage loadTestImage(String imagePath) {
        try {
            return Rainbow4J.loadImage(getClass().getResource(imagePath).getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(dataProvider="provideBadSamples")
    public void shouldGiveError(ValidationResult expectedResult, Spec spec, MockedPage page) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("object", spec).getError();
        
        assertThat(error, is(notNullValue()));
        assertThat(error, is(expectedResult.getError()));
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
          }})),
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

          row(specInside("container"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 20, 100, 100));
              put("container", element(10, 10, 110, 110));
          }})),

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
                // checking that it allows 2 pixels overlap
          row(specInside("container", location(exact(5), TOP)), page(new HashMap<String, PageElement>(){{
            put("object", element(0, 5, 102, 97));
            put("container", element(0, 0, 100, 100));
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
          row(specWidth(Range.exact(new RangeValue(333, 1)).withPercentOf("main-container-1/width")), page(new HashMap<String, PageElement>(){{
              put("object", element(0, 0, 100, 50));
              put("main-container-1", element(0, 0, 300, 50));
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
          
          row(specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(20, 10, 10, 10));
          }})),
          row(specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 15, 10, 10));
              put("item",  element(20, 10, 10, 20));
          }})),
          row(specHorizontally(Alignment.TOP, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(20, 10, 10, 20));
          }})),
          row(specHorizontally(Alignment.BOTTOM, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 40, 10, 10));
              put("item",  element(20, 30, 10, 20));
          }})),
          row(specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 20));
              put("item",  element(20, 10, 10, 20));
          }})),
          row(specHorizontally(Alignment.ALL, "item", 1), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 20));
              put("item",  element(20, 11, 10, 21));
          }})),
          

          // Vertically 
          
          row(specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(10, 20, 10, 10));
          }})),
          row(specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(15, 10, 10, 10));
              put("item",  element(10, 20, 20, 10));
          }})),
          row(specVertically(Alignment.LEFT, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(10, 20, 20, 10));
          }})),
          row(specVertically(Alignment.RIGHT, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(30, 10, 10, 10));
              put("item",  element(20, 20, 20, 10));
          }})),
          row(specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(10, 20, 10, 10));
          }})),
          row(specVertically(Alignment.ALL, "item", 1), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10));
              put("item",  element(11, 20, 11, 10));
          }})),
          
          
          // Text validation 

          row(specTextIs("Some text"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),

          row(specTextIs("some text").withOperations(asList("lowercase")), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10).withText("Some teXt"));
          }})),

          row(specTextIs("SOME TEXT").withOperations(asList("uppercase")), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("Some Text"));
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

          row(specTextMatches(".* some.* multiline"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 10, 10).withText("A text with some \n more multiline"));
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


            // Left of

            row(specLeftOf("button", Range.exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(40, 10, 10, 10));
            }})),

            row(specLeftOf("button", Range.between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 10, 10));
                put("button",  element(43, 10, 10, 10));
            }})),

            // Right of

            row(specRightOf("button", Range.exact(20)), page(new HashMap<String, PageElement>(){{
                put("object", element(40, 10, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})),

            row(specRightOf("button", Range.between(20, 25)), page(new HashMap<String, PageElement>(){{
                put("object", element(43, 10, 10, 10));
                put("button",  element(10, 10, 10, 10));
            }})),

          
          // Centered Inside 
          
          row(specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 80, 80));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 81, 81));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.ALL).withErrorRate(2), page(new HashMap<String, PageElement>(){{
              put("object", element(9, 9, 80, 80));
              put("container",  element(0, 0, 100, 100));
          }})),
          row(specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY).withErrorRate(2), page(new HashMap<String, PageElement>(){{
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
          
          row(specCenteredInside("container", SpecCentered.Alignment.VERTICALLY).withErrorRate(2), page(new HashMap<String, PageElement>(){{
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
          
          row(specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(90, 110, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn(TOP, LEFT, "container", location(exact(10), RIGHT), location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(110, 90, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn(TOP, LEFT, "container", location(exact(90), RIGHT), location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(190, 110, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn(TOP, LEFT, "container", location(exact(90), RIGHT), location(exact(20), BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(190, 120, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn(BOTTOM, RIGHT, "container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
              put("object", element(190, 180, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          row(specOn(BOTTOM, RIGHT, "container", location(exact(10), RIGHT), location(exact(20), BOTTOM)), page(new HashMap<String, PageElement>(){{
              put("object", element(210, 220, 50, 50));
              put("container", element(100, 100, 100, 100));
          }})),
          
          
          // Color Scheme
          
          row(specColorScheme(new ColorRange(Color.white, between(46, 52)), new ColorRange(Color.black, between(34, 40))), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 400, 300));
          }}, testImage)),
          
          row(specColorScheme(new ColorRange(Color.white, exact(100))), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 50, 40));
          }}, testImage)),




          // Image comparison

          row(specImage(asList("/imgs/button-sample-correct.png"), 1, PIXEL_UNIT, 0, 5), page(new HashMap<String, PageElement>(){{
              put("object", element(100, 90, 100, 40));
          }}, imageComparisonTestScreenshot)),

          row(specImage(asList("/imgs/page-sample-correct.png"), 2, PIXEL_UNIT, 0, 5, new Rect(40, 40, 100, 40)), page(new HashMap<String, PageElement>() {{
              put("object", element(100, 90, 100, 40));
          }}, imageComparisonTestScreenshot)),

          row(specImage(asList("/imgs/button-sample-incorrect.png", "/imgs/button-sample-correct.png"), 1, PIXEL_UNIT, 0, 5), page(new HashMap<String, PageElement>(){{
              put("object", element(100, 90, 100, 40));
          }}, imageComparisonTestScreenshot)),


            row(new SpecCss("font-size", SpecText.Type.IS, "18px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})),
            row(new SpecCss("font-size", SpecText.Type.ENDS, "px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})),
            row(new SpecCss("font-size", SpecText.Type.STARTS, "18"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})),
            row(new SpecCss("font-size", SpecText.Type.CONTAINS, "8p"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})),
            row(new SpecCss("font-size", SpecText.Type.MATCHES, "[0-9]+px"), page(new HashMap<String, PageElement>(){{
                put("object", elementWithCss("font-size", "18px"));
            }})),


            row(new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
            }})),
            row(new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", lessThan(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
            }})),
            row(new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", greaterThan(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("menu-item-4", element(0,0, 10,10));
            }})),
            row(new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", between(3, 5)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("menu-item-4", element(0,0, 10,10));
            }})),
            row(new SpecCount(SpecCount.FetchType.ANY, "menu-item-*, box-*", exact(4)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", element(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", element(0,0, 10,10));
                put("box-123", element(0,0, 10,10));
            }})),
            row(new SpecCount(SpecCount.FetchType.VISIBLE, "menu-item-*", exact(1)), page(new HashMap<String, PageElement>() {{
                put("object", element(0, 0, 10, 10));
                put("menu-item-1", invisibleElement(0, 0, 10, 10));
                put("menu-item-2", element(0, 0, 10, 10));
                put("menu-item-3", invisibleElement(0, 0, 10, 10));
                put("menu-item-4", invisibleElement(0, 0, 10, 10));
            }})),
            row(new SpecCount(SpecCount.FetchType.ABSENT, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>(){{
                put("object", element(0,0, 10,10));
                put("menu-item-1", absentElement(0,0, 10,10));
                put("menu-item-2", element(0,0, 10,10));
                put("menu-item-3", absentElement(0,0, 10,10));
                put("menu-item-4", absentElement(0,0, 10,10));
            }})),
        };
    }



    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
          // Contains

          row(validationResult(areas(new ValidationObject(new Rect(9, 11, 10, 10), "menu"), new ValidationObject(new Rect(10, 10, 100, 100), "object")), messages("\"menu\" is outside \"object\"")),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(9, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),

          row(validationResult(areas(new ValidationObject(new Rect(50, 50, 110, 10), "menu"), new ValidationObject(new Rect(10, 10, 101, 40), "button"), new ValidationObject(new Rect(10, 10, 100, 100), "object")), messages("\"menu\" is outside \"object\"", "\"button\" is outside \"object\"")),
              specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", element(50, 50, 110, 10));
                  put("button", element(10, 10, 101, 40));
          }})),

          row(validationResult(NO_AREA, messages("\"menu\" is not visible on page")),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", invisibleElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),

          row(validationResult(NO_AREA, messages("\"menu\" is absent on page")),
              specContains(CONTAINS_FULLY, "menu", "button"), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
                  put("menu", absentElement(11, 11, 10, 10));
                  put("button", element(60, 50, 40, 40));
          }})),

          row(validationResult(areas(new ValidationObject(new Rect(350, 10, 10, 10), "menu-item-3"), new ValidationObject(new Rect(0, 0, 200, 100), "object")), messages("\"menu-item-3\" is outside \"object\"")),
                  specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 0, 200, 100));
                      put("menu-item-1", element(10, 10, 10, 10));
                      put("menu-item-2", element(30, 10, 10, 10));
                      put("menu-item-3", element(350, 10, 10, 10));
                      put("button", element(70, 10, 10, 10));
          }})),
          row(validationResult(NO_AREA, messages("There are no objects matching: menu-item-*")),
                  specContains(CONTAINS_FULLY, "menu-item-*", "button"), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 0, 200, 100));
                      put("button", element(70, 10, 10, 10));
          }})),

          // Absent

          row(validationResult(singleArea(new Rect(10, 10, 100, 100), "object"), messages("\"object\" is not absent on page")),
              specAbsent(), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 100, 100));
          }})),

          // Visible

          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
              specVisible(), page(new HashMap<String, PageElement>(){{
                  put("object", invisibleElement(10, 10, 100, 100));
          }})),

          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
              specVisible(), page(new HashMap<String, PageElement>(){{
                  put("blabla", absentElement(10, 10, 100, 100));
          }})),

          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specVisible(), page(new HashMap<String, PageElement>(){{
                  put("object", absentElement(10, 10, 100, 100));
              }})),



          // Inside

          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                          messages("\"object\" is not completely inside. The offset is 380px.")),
                  specInside("container"), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 500, 50));
                      put("container", element(0, 0, 130, 120));
          }})),

          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is not completely inside. The offset is 380px.")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 500, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is not completely inside. The offset is 380px.")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(10, 10, 500, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(190, 110, 500, 500), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is 180px left instead of 10px")),
              specInsidePartly("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(190, 110, 500, 500));
                  put("container", element(10, 10, 100, 100));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is 30px left instead of 10px")),
              specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 20, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is 30px left and 20px top instead of 10px")),
                  specInside("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 20, 50, 50));
                      put("container", element(0, 0, 130, 120));
              }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is 50px right instead of 10px")),
              specInside("container", location(exact(10), RIGHT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 20, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is 20px top instead of 10px")),
              specInside("container", location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 20, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")), messages("\"object\" is 60px bottom instead of 10px")),
              specInside("container", location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")), messages("\"object\" is 30px left which is not in range of 10 to 20px")),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"container\" in page spec")),
              specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 10, 50, 50));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(30, 5, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                  messages("\"object\" is 30px left instead of 10px, is 5px top instead of 20px")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),


          row(validationResult(areas(new ValidationObject(new Rect(30, 5, 10, 50), "object"), new ValidationObject(new Rect(0, 0, 50, 120), "container")),
                          messages("\"object\" is 60% [30px] left instead of 20% [10px]")),
                  specInside("container", location(exact(20).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 5, 10, 50));
                      put("container", element(0, 0, 50, 120));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(30, 5, 10, 50), "object"), new ValidationObject(new Rect(0, 0, 50, 120), "container")),
                          messages("\"object\" is 60% [30px] left which is not in range of 20 to 40% [10 to 20px]")),
                  specInside("container", location(between(20, 40).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(30, 5, 10, 50));
                      put("container", element(0, 0, 50, 120));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", absentElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", invisibleElement(30, 5, 50, 50));
                  put("container", element(0, 0, 130, 120));
          }})),
          
          row(validationResult(NO_AREA, messages("\"container\" is absent on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", absentElement(0, 0, 130, 120));
          }})),
          
          row(validationResult(NO_AREA, messages("\"container\" is not visible on page")),
              specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                  put("object", element(30, 5, 50, 50));
                  put("container", invisibleElement(0, 0, 130, 120));
          }})),
          
          
          // Near 
          row(validationResult(areas(new ValidationObject(new Rect(90, 5, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                  messages("\"object\" is 10px left instead of 30px")),
                  specNear("button", location(exact(30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(90, 5, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                  messages("\"object\" is 10px left which is not in range of 20 to 30px")),
                  specNear("button", location(between(20, 30), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 5, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
              
          row(validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                  messages("\"object\" is 10px left and 20px top instead of 30px")),
                  specNear("button", location(exact(30), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(310, 250, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 100, 50), "button")),
                  messages("\"object\" is 10px right instead of 30px, is 0px bottom which is not in range of 10 to 20px")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          

          row(validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 50, 50), "button")),
                  messages("\"object\" is 20% [10px] left instead of 40% [20px]")),
                  specNear("button", location(exact(40).withPercentOf("button/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 50, 50));
          }})),
          
          row(validationResult(areas(new ValidationObject(new Rect(90, 130, 100, 50), "object"), new ValidationObject(new Rect(200, 200, 50, 50), "button")),
                  messages("\"object\" is 20% [10px] left which is not in range of 40 to 50% [20 to 25px]")),
                  specNear("button", location(between(40, 50).withPercentOf("button/area/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                      put("object", element(90, 130, 100, 50));
                      put("button", element(200, 200, 50, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
                      put("button", element(200, 200, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"button\" is absent on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
                      put("button", invisibleElement(200, 200, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"button\" in page spec")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(310, 250, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specNear("button", location(exact(30), RIGHT), location(between(10, 20), BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("button", absentElement(200, 200, 100, 50));
          }})),
          
          
          // Width 
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px instead of 10px")),
                  specWidth(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be greater than 110px")),
                  specWidth(Range.greaterThan(110)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px but it should be less than 90px")),
                  specWidth(Range.lessThan(90)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 100px which is not in range of 10 to 40px")),
                  specWidth(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),

          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] instead of 10% [20px]")),
                  specWidth(exact(10).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 200, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" width is 50% [100px] which is not in range of 10 to 20% [20 to 40px]")),
                  specWidth(between(10, 20).withPercentOf("container/width")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 200, 50));
          }})),
          
          
          // Height 
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>())),
                  
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(310, 250, 100, 50));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(310, 250, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px instead of 10px")),
                  specHeight(Range.exact(10)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 50px which is not in range of 10 to 40px")),
                  specHeight(Range.between(10, 40)), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] instead of 10% [20px]")),
                  specHeight(exact(10).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 100, 200));
          }})),
          
          row(validationResult(singleArea(new Rect(100, 100, 100, 50), "object"), messages("\"object\" height is 25% [50px] which is not in range of 10 to 15% [20 to 30px]")),
                  specHeight(between(10, 15).withPercentOf("container/height")), page(new HashMap<String, PageElement>(){{
                      put("object", element(100, 100, 100, 50));
                      put("container", element(100, 100, 100, 200));
          }})),
          
          // Horizontally 
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"item\" in page spec")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
          }})),
          row(validationResult(NO_AREA, messages("\"item\" is not visible on page")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", invisibleElement(10, 10, 10, 20));
          }})),
          row(validationResult(NO_AREA, messages("\"item\" is absent on page")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", absentElement(10, 10, 10, 15));
          }})),
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("item", element(10, 10, 50, 10));
          }})),
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 15));
          }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 15));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 50, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 15), "item")),
                  messages("\"item\" is not aligned horizontally centered with \"object\". Offset is 2px")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 15));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 50, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 20), "item")),
                  messages("\"item\" is not aligned horizontally centered with \"object\". Offset is 5px")),
                  specHorizontally(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 20));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 15, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 20), "item")),
                  messages("\"item\" is not aligned horizontally top with \"object\". Offset is 5px")),
                  specHorizontally(Alignment.TOP, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 15, 10, 10));
                      put("item", element(10, 10, 10, 20));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 10, 10, 5), "item")),
                  messages("\"item\" is not aligned horizontally bottom with \"object\". Offset is 5px")),
                  specHorizontally(Alignment.BOTTOM, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item", element(10, 10, 10, 5));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(30, 10, 10, 5), "item")),
                  messages("\"item\" is not aligned horizontally all with \"object\". Offset is 5px")),
                  specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item", element(30, 10, 10, 5));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(30, 10, 15, 5), "item")),
                  messages("\"item\" is not aligned horizontally all with \"object\". Offset is 5px")),
                  specHorizontally(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item", element(30, 10, 15, 5));
          }})),
          
          
          // Vertically 
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"item\" in page spec")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
          }})),
          row(validationResult(NO_AREA, messages("\"item\" is not visible on page")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", invisibleElement(10, 10, 10, 20));
          }})),
          row(validationResult(NO_AREA, messages("\"item\" is absent on page")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 50, 10));
                      put("item", absentElement(10, 10, 10, 15));
          }})),
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("item", element(10, 10, 50, 10));
          }})),
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 20));
          }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 50, 10));
                      put("item", element(10, 10, 10, 20));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(10, 20, 10, 10), "item")),
                  messages("\"item\" is not aligned vertically centered with \"object\". Offset is 5px")),
                  specVertically(Alignment.CENTERED, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item", element(10, 20, 10, 10));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(5, 20, 10, 10), "item")),
                  messages("\"item\" is not aligned vertically left with \"object\". Offset is 5px")),
                  specVertically(Alignment.LEFT, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item", element(5, 20, 10, 10));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 20, 10), "object"), new ValidationObject(new Rect(10, 30, 10, 10), "item")),
                  messages("\"item\" is not aligned vertically right with \"object\". Offset is 10px")),
                  specVertically(Alignment.RIGHT, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 20, 10));
                      put("item", element(10, 30, 10, 10));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(10, 30, 5, 10), "item")),
                  messages("\"item\" is not aligned vertically all with \"object\". Offset is 5px")),
                  specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item", element(10, 30, 5, 10));
          }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 10, 10), "object"), new ValidationObject(new Rect(15, 30, 5, 10), "item")),
                  messages("\"item\" is not aligned vertically all with \"object\". Offset is 5px")),
                  specVertically(Alignment.ALL, "item", 0), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10));
                      put("item", element(15, 30,   5, 10));
          }})),
          
          
          // Text validation 
          
          row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>())),
                  
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 10, 10));
          }})),
          
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 10, 10));
          }})),
          
          row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should be \"some wrong text\"")),
                  specTextIs("some wrong text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should contain \"good\"")),
                  specTextContains("good"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should start with \"text\"")),
                  specTextStarts("text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should end with \"Some\"")),
                  specTextEnds("Some"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),
          
          row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" text is \"Some text\" but should match \"Some [0-9]+ text\"")),
                  specTextMatches("Some [0-9]+ text"), 
                  page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 10, 10).withText("Some text"));
          }})),


          // Css
            row(validationResult(NO_AREA, messages("Cannot find locator for \"object\" in page spec")),
                    new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                    page(new HashMap<String, PageElement>())),

            row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                    new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", invisibleElement(10, 10, 10, 10));
                    }})),

            row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                    new SpecCss("font-size", SpecText.Type.IS, "some wrong text"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", absentElement(10, 10, 10, 10));
                    }})),

            row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should be \"19px\"")),
                    new SpecCss("font-size", SpecText.Type.IS, "19px"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", elementWithCss("font-size", "18px"));
                    }})),

            row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should start with \"19\"")),
                    new SpecCss("font-size", SpecText.Type.STARTS, "19"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", elementWithCss("font-size", "18px"));
                    }})),

            row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should end with \"em\"")),
                    new SpecCss("font-size", SpecText.Type.ENDS, "em"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", elementWithCss("font-size", "18px"));
                    }})),

            row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should contain \"9\"")),
                    new SpecCss("font-size", SpecText.Type.CONTAINS, "9"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", elementWithCss("font-size", "18px"));
                    }})),

            row(validationResult(singleArea(new Rect(10, 10, 10, 10), "object"), messages("\"object\" css property \"font-size\" is \"18px\" but should match \"[0-9]+em\"")),
                    new SpecCss("font-size", SpecText.Type.MATCHES, "[0-9]+em"),
                    page(new HashMap<String, PageElement>(){{
                        put("object", elementWithCss("font-size", "18px"));
                    }})),

          // Above
          
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),    
          row(validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", invisibleElement(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"button\" is absent on page")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", absentElement(10, 60, 10, 10));
              }})),    
          row(validationResult(areas(new ValidationObject(new Rect(10, 40, 10, 10), "object"), new ValidationObject(new Rect(10, 60, 10, 10), "button")),
                  messages("\"object\" is 10px above \"button\" instead of 20px")),
                  specAbove("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(validationResult(areas(new ValidationObject(new Rect(10, 40, 10, 10), "object"), new ValidationObject(new Rect(10, 60, 10, 10), "button")),
                  messages("\"object\" is 10px above \"button\" which is not in range of 20 to 30px")),
                  specAbove("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
              
              
          // Below 
              
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("button", element(10, 60, 10, 10));
              }})),    
          row(validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", invisibleElement(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"button\" is absent on page")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("button", absentElement(10, 60, 10, 10));
              }})),    
          row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                  messages("\"object\" is 10px below \"button\" instead of 20px")),
                  specBelow("button", exact(20)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 60, 10, 10));
                      put("button", element(10, 40, 10, 10));
              }})),
              row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                  messages("\"object\" is 10px below \"button\" which is not in range of 20 to 30px")),
                  specBelow("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 60, 10, 10));
                      put("button", element(10, 40, 10, 10));
              }})),


            // Left of

            row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                    specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", invisibleElement(10, 40, 10, 10));
                        put("button", element(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                    specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", absentElement(10, 40, 10, 10));
                        put("button", element(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                    specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 40, 10, 10));
                        put("button", invisibleElement(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"button\" is absent on page")),
                    specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 40, 10, 10));
                        put("button", absentElement(10, 60, 10, 10));
                    }})),
            row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                            messages("\"object\" is 40px left of \"button\" instead of 20px")),
                    specLeftOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 10, 10, 10));
                        put("button", element(60, 10, 10, 10));
                    }})),
            row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                            messages("\"object\" is 40px left of \"button\" which is not in range of 20 to 30px")),
                    specLeftOf("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 10, 10, 10));
                        put("button", element(60, 10, 10, 10));
                    }})),

            // Left of

            row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                    specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", invisibleElement(10, 40, 10, 10));
                        put("button", element(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                    specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", absentElement(10, 40, 10, 10));
                        put("button", element(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"button\" is not visible on page")),
                    specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 40, 10, 10));
                        put("button", invisibleElement(10, 60, 10, 10));
                    }})),
            row(validationResult(NO_AREA, messages("\"button\" is absent on page")),
                    specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(10, 40, 10, 10));
                        put("button", absentElement(10, 60, 10, 10));
                    }})),
            row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                            messages("\"object\" is 40px right of \"button\" instead of 20px")),
                    specRightOf("button", exact(20)), page(new HashMap<String, PageElement>(){{
                        put("object", element(60, 10, 10, 10));
                        put("button", element(10, 10, 10, 10));
                    }})),
            row(validationResult(areas(new ValidationObject(new Rect(10, 60, 10, 10), "object"), new ValidationObject(new Rect(10, 40, 10, 10), "button")),
                            messages("\"object\" is 40px right of \"button\" which is not in range of 20 to 30px")),
                    specRightOf("button", between(20, 30)), page(new HashMap<String, PageElement>(){{
                        put("object", element(60, 10, 10, 10));
                        put("button", element(10, 10, 10, 10));
                    }})),

            // Centered
      
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 10, 10));
                      put("container", element(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 10, 10));
                      put("container", element(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"container\" is absent on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("container", absentElement(10, 60, 10, 10));
              }})),
          row(validationResult(NO_AREA, messages("\"container\" is not visible on page")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 10, 10));
                      put("container", invisibleElement(10, 60, 10, 10));
              }})),
                      
          row(validationResult(areas(new ValidationObject(new Rect(20, 20, 80, 60), "object"), new ValidationObject(new Rect(0, 0, 100, 100), "container")),
                  messages("\"object\" is not centered horizontally inside \"container\". Offset is 20px")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 20, 80, 60));
                      put("container", element(0, 0, 100, 100));
              }})),
              
              
          row(validationResult(areas(new ValidationObject(new Rect(20, 20, 75, 60), "object"), new ValidationObject(new Rect(0, 0, 100, 100), "container")),
                  messages("\"object\" is not centered horizontally inside \"container\". Offset is 15px")),
                  specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY, 10), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 20, 75, 60));
                      put("container", element(0, 0, 100, 100));
              }})),    
        
          row(validationResult(areas(new ValidationObject(new Rect(0, 20, 120, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is centered but not horizontally inside \"container\"")),
                  specCenteredInside("container", SpecCentered.Alignment.ALL), page(new HashMap<String, PageElement>(){{
                      put("object", element(0, 20, 120, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(validationResult(areas(new ValidationObject(new Rect(20, 10, 100, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is not centered vertically inside \"container\". Offset is 40px")),
                  specCenteredInside("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 100, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is not centered horizontally inside \"container\". Offset is 70px")),
                  specCenteredInside("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is not centered vertically on \"container\". Offset is 40px")),
                  specCenteredOn("container", SpecCentered.Alignment.VERTICALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          row(validationResult(areas(new ValidationObject(new Rect(20, 10, 10, 60), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                  messages("\"object\" is not centered horizontally on \"container\". Offset is 70px")),
                  specCenteredOn("container", SpecCentered.Alignment.HORIZONTALLY), page(new HashMap<String, PageElement>(){{
                      put("object", element(20, 10, 10, 60));
                      put("container", element(10, 10, 100, 100));
                  }})),
          
                  
           // On
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 40, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 40, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(validationResult(NO_AREA, messages("\"container\" is not visible on page")),
                  specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 50, 50));
                      put("container", invisibleElement(100, 100, 100, 100));
              }})),
          row(validationResult(NO_AREA, messages("\"container\" is absent on page")),
                  specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 40, 50, 50));
                      put("container", absentElement(100, 100, 100, 100));
              }})),
          row(validationResult(areas(new ValidationObject(new Rect(95, 110, 50, 50), "object"), new ValidationObject(new Rect(100, 100, 100, 100), "container")),
                  messages("\"object\" is 5px left instead of 10px")),
                  specOn(TOP, LEFT, "container", location(exact(10), LEFT, BOTTOM)), page(new HashMap<String, PageElement>(){{
                      put("object", element(95, 110, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
          row(validationResult(areas(new ValidationObject(new Rect(105, 90, 50, 50), "object"), new ValidationObject(new Rect(100, 100, 100, 100), "container")),
                  messages("\"object\" is 5px right which is not in range of 10 to 15px, is 10px top instead of 5px")),
                  specOn(TOP, LEFT, "container", location(between(10, 15), RIGHT), location(exact(5), TOP)), page(new HashMap<String, PageElement>(){{
                      put("object", element(105, 90, 50, 50));
                      put("container", element(100, 100, 100, 100));
              }})),
              
              
          // Color Scheme
          row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                  specColorScheme(new ColorRange(Color.black, between(30, 33))), page(new HashMap<String, PageElement>(){{
                      put("object", invisibleElement(10, 10, 400, 300));
              }}, testImage)),
          row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specColorScheme(new ColorRange(Color.black, between(30, 33))), page(new HashMap<String, PageElement>(){{
                      put("object", absentElement(10, 10, 400, 300));
              }}, testImage)),
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 400, 300), "object")),
                  messages("color #000000 on \"object\" is 36% which is not in range of 10 to 20%")),
                  specColorScheme(new ColorRange(Color.black, between(10, 20))), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 400, 300));
              }}, testImage)),
              
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 400, 300), "object")),
                  messages("color #ffffff on \"object\" is 48% instead of 30%")),
                  specColorScheme(new ColorRange(Color.white, exact(30))), page(new HashMap<String, PageElement>(){{
                      put("object", element(10, 10, 400, 300));
              }}, testImage)),
              
          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 300), "object")),
                      messages("color #3a70d0 on \"object\" is 12% instead of 30%")),
                      specColorScheme(new ColorRange(Color.decode("#3A70D0"), exact(30))), page(new HashMap<String, PageElement>(){{
                          put("object", element(10, 10, 500, 300));
                  }}, testImage)),

          row(validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 300), "object")),
                    messages("color #3a70d0 on \"object\" is 12.87% instead of 12.84%")),
                specColorScheme(new ColorRange(Color.decode("#3A70D0"), exact(new RangeValue(1284, 2)))), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 300));
                }}, testImage)),



          // Image comparsion
            row(validationResult(NO_AREA, messages("\"object\" is absent on page")),
                  specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, true, 0, 10), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(10, 10, 400, 300));
                }}, testImage)),

            row(validationResult(NO_AREA, messages("\"object\" is not visible on page")),
                specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, true, 0, 10), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(10, 10, 400, 300));
                }}, testImage)),

            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                        new ValidationError(messages("Element does not look like \"/imgs/button-sample-incorrect.png\". " +
                                "There are 3820 mismatching pixels but max allowed is 600"))),
                specImage(asList("/imgs/button-sample-incorrect.png"), 600, PIXEL_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                    }}, imageComparisonTestScreenshot)),

            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                            new ValidationError(messages("Element does not look like \"/imgs/button-sample-incorrect.png\". " +
                                    "There are 95.5% mismatching pixels but max allowed is 2%"))),
                    specImage(asList("/imgs/button-sample-incorrect.png"), 2.0, PERCENTAGE_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                    }}, imageComparisonTestScreenshot)),

            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                            new ValidationError(messages("Couldn't load image: /imgs/undefined-image.png"))),
                    specImage(asList("/imgs/undefined-image.png"), 1.452, PERCENTAGE_UNIT, 0, 10), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                    }}, imageComparisonTestScreenshot)),


            /* Spec Count */

            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                            new ValidationError(messages("There are 3 objects matching \"menu-item-*\" instead of 2"))),
                    new SpecCount(SpecCount.FetchType.ANY, "menu-item-*", exact(2)), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                        put("menu-item-1", element(100, 90, 100, 40));
                        put("menu-item-2", element(100, 90, 100, 40));
                        put("menu-item-3", element(100, 90, 100, 40));
                    }})),
            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                            new ValidationError(messages("There are 2 visible objects matching \"menu-item-*\" instead of 3"))),
                    new SpecCount(SpecCount.FetchType.VISIBLE, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                        put("menu-item-1", element(100, 90, 100, 40));
                        put("menu-item-2", element(100, 90, 100, 40));
                        put("menu-item-3", absentElement(100, 90, 100, 40));
                    }})),
            row(new ValidationResult(NO_SPEC, areas(new ValidationObject(new Rect(100, 90, 100, 40), "object")),
                            new ValidationError(messages("There are 1 absent objects matching \"menu-item-*\" instead of 3"))),
                    new SpecCount(SpecCount.FetchType.ABSENT, "menu-item-*", exact(3)), page(new HashMap<String, PageElement>() {{
                        put("object", element(100, 90, 100, 40));
                        put("menu-item-1", element(100, 90, 100, 40));
                        put("menu-item-2", element(100, 90, 100, 40));
                        put("menu-item-3", absentElement(100, 90, 100, 40));
                    }}))
        };
    }

    private ValidationResult validationResult(List<ValidationObject> areas, List<String> messages) {
        return new ValidationResult(NO_SPEC, areas, new ValidationError(messages));
    }

    @Test
    public void imageSpec_shouldAlsoGenerate_imageComparisonMap() {
        MockedPage page = page(new HashMap<String, PageElement>() {{
            put("object", element(100, 90, 100, 40));
        }}, imageComparisonTestScreenshot);

        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("object", specImage(asList("/imgs/button-sample-incorrect.png"), 0, PIXEL_UNIT, 0, 10)).getError();


        assertThat("Comparison map should not be null", error.getImageComparison().getComparisonMap(), is(notNullValue()));
    }
    
    private List<ValidationObject> areas(ValidationObject...errorAreas) {
        return asList(errorAreas);
    }

    private List<String> messages(String...messages) {
        return asList(messages);
    }

    private List<ValidationObject> singleArea(Rect rect, String tooltip) {
        return asList(new ValidationObject(rect, tooltip));
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
    
    private SpecVertically specVertically(Alignment alignment, String objectName, int errorRate) {
        SpecVertically spec = new SpecVertically(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }

    private SpecHorizontally specHorizontally(Alignment alignment, String objectName, int errorRate) {
        SpecHorizontally spec =  new SpecHorizontally(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }
    
    private SpecHeight specHeight(Range range) {
        return new SpecHeight(range);
    }

    private SpecWidth specWidth(Range range) {
        return new SpecWidth(range);
    }

    private SpecNear specNear(String secondObjectName, Location...locations) {
        return new SpecNear(secondObjectName, asList(locations));
    }

    private SpecInside specInside(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, asList(locations));
    }

    private SpecInside specInsidePartly(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, asList(locations)).withPartlyCheck();
    }

    private SpecOn specOn(Side sideHorizontal, Side sideVertical, String parentObjectName, Location...locations) {
        return new SpecOn(parentObjectName, sideHorizontal, sideVertical, asList(locations));
    }

    private Location location(Range exact, Side...sides) {
        return new Location(exact, asList(sides));
    }

    private MockedPage page(HashMap<String, PageElement> elements) {
        return new MockedPage(elements);
    }
    
    private MockedPage page(HashMap<String, PageElement> elements, BufferedImage screenshotImage) {
        return new MockedPage(elements, screenshotImage);
    }

    private MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }

    private PageElement elementWithCss(String cssPropertyName, String value) {
        return new MockedPageElement(10,10,10,10).withCssProperty(cssPropertyName, value);
    }

    protected PageElement invisibleElement(int left, int top, int width, int height) {
        return new MockedInvisiblePageElement(left, top, width, height);
    }
    
    private MockedPageElement absentElement(int left, int top, int width, int height) {
        return new MockedAbsentPageElement(left, top, width, height);
    }
    
    private SpecContains specContains(boolean isPartly, String...objects) {
        return new SpecContains(asList(objects), isPartly);
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
        return new SpecCentered(object, alignment, SpecCentered.Location.ON).withErrorRate(2);
    }

    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE);
    }
    
    private SpecCentered specCenteredInside(String object, SpecCentered.Alignment alignment, int errorRate) {
        return new SpecCentered(object, alignment, SpecCentered.Location.INSIDE).withErrorRate(errorRate);
    }

    private SpecLeftOf specLeftOf(String object, Range range) {
        return new SpecLeftOf(object, range);
    }

    private SpecRightOf specRightOf(String object, Range range) {
        return new SpecRightOf(object, range);
    }

    private SpecColorScheme specColorScheme(ColorRange...colorRanges) {
        SpecColorScheme spec = new SpecColorScheme();
        spec.setColorRanges(asList(colorRanges));
        return spec;
    }

    private SpecImage specImage(List<String> imagePaths, double errorValue, boolean isPixelUnit, int pixelSmooth, int tolerance) {
        return specImage(imagePaths, errorValue, isPixelUnit, pixelSmooth, tolerance, null);
    }

    private SpecImage specImage(List<String> imagePaths, double errorValue, boolean isPixelUnit, int blur, int tolerance, Rect selectedArea) {
        SpecImage spec = new SpecImage();

        if (isPixelUnit) {
            spec.setErrorRate(new SpecImage.ErrorRate(errorValue, SpecImage.ErrorRateType.PIXELS));
        }
        else {
            spec.setErrorRate(new SpecImage.ErrorRate(errorValue, SpecImage.ErrorRateType.PERCENT));
        }

        spec.setImagePaths(imagePaths);

        List<ImageFilter> filters = new LinkedList<>();
        spec.setOriginalFilters(filters);
        spec.setSampleFilters(filters);

        if (blur > 0) {
            filters.add(new BlurFilter(blur));
        }
        spec.setTolerance(tolerance);
        spec.setSelectedArea(selectedArea);
        return spec;
    }

    public Object[] row (Object...args) {
        return args;
    }
}
