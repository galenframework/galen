/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.specs.reader;

import static net.mindengine.galen.specs.page.CorrectionsRect.simpleCorrectionRect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.CorrectionsRect;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.StateObjectDefinition;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ObjectDefinitionReaderTest {
    
    private static final Browser NO_BROWSER = null;


    @Test(dataProvider = "provideGoodSamples")
    public void shouldParseCorrect_objectDefinition(String objectDefinitionText, String expectedName, Locator expectedLocator) {
        PageSpec pageSpec = new PageSpec();
        new StateObjectDefinition(pageSpec, new PageSpecReader(NO_BROWSER)).process(objectDefinitionText);
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
            row("my-object-123  @(0,0,-1,-1)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(simpleCorrectionRect(0, 0, -1, -1))),
            row("my-object-123  @  (0,0,-1,-1)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(simpleCorrectionRect(0, 0, -1, -1))),
            row("my-object-123  @(10, 20, +5, +30)  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(simpleCorrectionRect(10, 20, 5, 30))),
            row("my-object-123  @ ( 0 , 0 , 4, -5 )  css   #qwe", "my-object-123", new Locator("css", "#qwe").withCorrections(simpleCorrectionRect(0, 0, 4, -5))),
            row("my-object-123  @ ( 0 , 0 , =40, =30 )  css   #qwe", "my-object-123", 
                    new Locator("css", "#qwe").withCorrections(
                            new CorrectionsRect(new CorrectionsRect.Correction(0, CorrectionsRect.Type.PLUS),
                                    new CorrectionsRect.Correction(0, CorrectionsRect.Type.PLUS),
                                    new CorrectionsRect.Correction(40, CorrectionsRect.Type.EQUALS),
                                    new CorrectionsRect.Correction(30, CorrectionsRect.Type.EQUALS)))),
        };
    }
    
    
    @Test(dataProvider = "provideBadSamples")
    public void shouldGiveError_forIncorrect_objectDefinitions(String objectDefinitionText, String expectedErrorMessage) {
        SyntaxException exception = null;
        try {
            PageSpec pageSpec = new PageSpec();
            new StateObjectDefinition(pageSpec, new PageSpecReader(NO_BROWSER)).process(objectDefinitionText);
        }
        catch (SyntaxException e) {
            exception = e;
        }
        
        assertThat("Exception should be", exception, is(notNullValue()));
        assertThat("Exception message should be", exception.getMessage(), is(expectedErrorMessage));
    }
    
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][] {
            row("myObject", 
                    "Missing locator for object \"myObject\""),
            row("myObject id", 
                    "Locator for object \"myObject\" is not defined correctly"),
            row("myObject #", 
                    "Locator for object \"myObject\" is not defined correctly"),
            row("myObject @ id some-id", 
                    "Error parsing corrections. Missing starting '(' symbol"),
            row("myObject @ 10, 20, 30, 40) id some-id", 
                    "Error parsing corrections. Missing starting '(' symbol"),
            row("myObject @() id some-id", 
                    "Error parsing corrections. No values provided"),
            row("myObject @ 10, 20, 30, 40 id some-id", 
                    "Error parsing corrections. Missing starting '(' symbol"),
            row("myObject @(10) id some-id", 
                    "Wrong number of arguments in corrections: 1"),
            row("myObject @(10, 20) id some-id", 
                    "Wrong number of arguments in corrections: 2"),
            row("myObject @(10, 20, 30) id some-id", 
                    "Wrong number of arguments in corrections: 3"),
            row("myObject @(10, 20, 30, 40, 50) id some-id", 
                    "Wrong number of arguments in corrections: 5"),
            row("myObject @(10, 20, 30, 40, 50, 60) id some-id", 
                    "Wrong number of arguments in corrections: 6"),
            row("myObject @(10, 20, 30, 40 id some-id", 
                    "Error parsing corrections. Missing closing ')' symbol"),
            row("myObject @(10 20 30 40) id some-id", 
                    "Wrong number of arguments in corrections: 1"),
            row("myObject @(10, 20, 30, 40)", 
                    "Missing locator for object \"myObject\""),
            row("myObject @(10, 20, 30, 40) id", 
                    "Locator for object \"myObject\" is not defined correctly"),
            row("  ", 
                    "Object name is not defined correctly"),
        };
    }
    
    
    public Object[] row(Object...args) {
        return args;
    }

}
