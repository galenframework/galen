package net.mindengine.galen.tests.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.HashMap;

import net.mindengine.galen.components.validation.MockedPage;
import net.mindengine.galen.components.validation.MockedPageElement;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ValidationTest {

    private static final boolean CONTAINS_FULLY = false;
    private static final boolean CONTAINS_PARTLY = true;


    @Test(dataProvider="provideGoodContainsSamples")
    public void shouldPassValidation_forSpec_contains(SpecContains spec, Page page) {
        PageValidation validation = new PageValidation(page, null);
        ValidationError error = validation.check("object", spec);
        
        assertThat(error, is(nullValue()));
    }
    
    //TODO make "contains partly" and simple "contains"
    
    
    @Test(dataProvider="provideBadContainsSamples")
    public void shouldGiveError_forSpec_contains(SpecContains spec, Page page, ValidationError expectedError) {
        PageValidation validation = new PageValidation(page, null);
        ValidationError error = validation.check("object", spec);
        
        assertThat(error, is(notNullValue()));
        assertThat(error, is(expectedError));
    }
    
    
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideGoodContainsSamples() {
        return new Object[][] {
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
        };
    }
    
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadContainsSamples() {
        return new Object[][] {
          row(specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(9, 11, 10, 10));
              put("button", element(60, 50, 40, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Object \"menu\" is outside the specified element")),
          
          row(specContains(false, "menu", "button"), page(new HashMap<String, PageElement>(){{
              put("object", element(10, 10, 100, 100));
              put("menu", element(50, 50, 110, 10));
              put("button", element(10, 10, 101, 40));
          }}), new ValidationError(new Rect(10, 10, 100, 100), "Objects \"menu\", \"button\" are outside the specified element")),
        };
    }
    
    
    private MockedPage page(HashMap<String, PageElement> elements) {
        return new MockedPage(elements);
    }

    private MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }
    
    private SpecContains specContains(boolean isPartly, String...objects) {
        return new SpecContains(Arrays.asList(objects), isPartly);
    }


    public Object[] row (Object...args) {
        return args;
    }
}
