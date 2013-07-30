package net.mindengine.galen.tests.specs.reader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.StateObjectDefinition;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ObjectDefinitionReaderTest {
    
    //TODO create all tests for object definitions
    //TODO introduce corrections
    
    
    @Test(dataProvider = "provideGoodSamples")
    public void shouldParseCorrect_objectDefinition(String objectDefinitionText, String expectedName, Locator expectedLocator) {
        PageSpec pageSpec = new PageSpec();
        new StateObjectDefinition(pageSpec).process(objectDefinitionText);
        assertThat(pageSpec.getObjects(), hasKey(expectedName));
        assertThat(pageSpec.getObjectLocator(expectedName), is(expectedLocator));
    }
    
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            row("myObject id my-object", "myObject", new Locator("id", "my-object")),
            row("myObject\tid\tmy-object", "myObject", new Locator("id", "my-object")),
            row("myObject xpath   //div[@name = \"auto's\"]", "myObject", new Locator("xpath", "//div[@name = \"auto's\"]")),
            row("myObject whatEver   sas fas f 3r 32r 1qwr ", "myObject", new Locator("whatEver", "sas fas f 3r 32r 1qwr")),
            row("my-object-123    css   .container div:first-child()", "my-object-123", new Locator("css", ".container div:first-child()")),
            row("my-object-123    css   #qwe", "my-object-123", new Locator("css", "#qwe")),
            row("my-object-123  corrections(0,0,-1,-1)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(0, 0, -1, -1)),
            row("my-object-123  corrections  (0,0,-1,-1)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(0, 0, -1, -1)),
            row("my-object-123  corrections(10, 20, +5, +30)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(10, 20, 5, 30)),
            row("my-object-123  corrections ( 0 , 0 , 4, -5 )  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(0, 0, 4, -5)),
        };
    }
    
    
    //TODO implement negative tests for object definitions
    
    public Object[] row(Object...args) {
        return args;
    }

}
