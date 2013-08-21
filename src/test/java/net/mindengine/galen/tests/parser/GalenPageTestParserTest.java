package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.suite.GalenPageTest;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GalenPageTestParserTest {

    
    @Test(dataProvider="provideGoodSamples") public void shouldParse_galenPageTest_successfully(String text, GalenPageTest expected) {
        GalenPageTest real = GalenPageTest.readFrom(text);
        assertThat(real, is(expected));
    }
    
    
    @DataProvider public Object[][] provideGoodSamples() {
        return new Object[][]{
           test("http://example.org 640x480", new GalenPageTest()
                   .withUrl("http://example.org")
                   .withSize(640, 480)),
                   
                   //TODO more tests samples
        };
    }


    private Object[] test(Object...args) {
        return args;
    }
}
