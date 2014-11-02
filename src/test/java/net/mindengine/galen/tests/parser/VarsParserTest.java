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
package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Properties;

import net.mindengine.galen.parser.VarsParser;
import net.mindengine.galen.parser.VarsParserJsFunctions;
import net.mindengine.galen.parser.VarsParserJsProcessor;
import net.mindengine.galen.suite.reader.Context;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class VarsParserTest {
    
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private VarsParserJsFunctions jsFunctions = new VarsParserJsFunctions() {
        @Override
        public int count(String regex) {
            if (regex.equals("testval1")){
                return 12;
            }
            else return 15;
        }
    };


    @Test(dataProvider="provideGoodSamples") public void shouldProcessTemplate_successfully(Integer number, Context context, String templateText, String expectedText) {
        VarsParser template = new VarsParser(context, EMPTY_PROPERTIES, new VarsParserJsProcessor(context, jsFunctions));
        String realText = template.parse(templateText);

        assertThat(realText, is(expectedText));
    }

    @Test
    public void shouldAllowTo_loadCustomJavascript() {
        Context context = new Context();
        VarsParser template = new VarsParser(context, EMPTY_PROPERTIES, new VarsParserJsProcessor(context, jsFunctions));
        String realText = template.parse("${load('/specs/customFunction.js')} got it from js: ${customFunction('qwe', 'ert')}");

        assertThat(realText, is(" got it from js: qwe-ert"));
    }
    
    
    @DataProvider public Object[][] provideGoodSamples() {
        return new Object[][] {
            {1, new Context().withParameter("name", "John"), "Hi my name is ${name}", "Hi my name is John"},
            {2, new Context().withParameter("name", "John"), "Hi my name is ${name} Connor", "Hi my name is John Connor"},
            {3, new Context().withParameter("name", "John"), "Hi my name is \\${name} Connor", "Hi my name is ${name} Connor"},
            {4, new Context().withParameter("name", "John"), "Hi my name is \\\\${name} Connor", "Hi my name is \\${name} Connor"},
            {5, new Context().withParameter("name", "John"), "Hi my name is ${ name } Connor", "Hi my name is John Connor"},
            {6, new Context(), "Hi my name is ${name} Connor", "Hi my name is  Connor"},
            {7, new Context().withParameter("name", "John").withParameter("surname", "Connor"), "Hi my name is ${name} ${surname}", "Hi my name is John Connor"},
            {8, new Context(), "I have some money $30", "I have some money $30"},
            {9, new Context(), "I have some money $30$", "I have some money $30$"},
            {10, new Context(), "I have some money ${ 30", "I have some money "},
            {11, new Context(), "There are ${count('testval1')} objects", "There are 12 objects"},
            {12, new Context(), "There are ${count(\"sdvdv\")*2 - 1} objects", "There are 29 objects"},
            {13, new Context().withParameter("qwe", 123), "Hi my age is ${qwe - 1}", "Hi my age is 122"},
        };
    }
}
