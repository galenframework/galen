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
package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.BashTemplate;
import net.mindengine.galen.suite.reader.Context;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BashTemplateTest {

    
    
    @Test(dataProvider="provideGoodSamples") public void shouldProcessTemplate_successfully(Context context, String templateText, String expectedText) {
        BashTemplate template = new BashTemplate(templateText);
        String realText = template.process(context);
        
        assertThat(realText, is(expectedText));
    }
    
    
    @DataProvider public Object[][] provideGoodSamples() {
        return new Object[][] {
            {new Context().withParameter("name", "John"), "Hi my name is ${name}", "Hi my name is John"},
            {new Context().withParameter("name", "John"), "Hi my name is ${name} Connor", "Hi my name is John Connor"},
            {new Context().withParameter("name", "John"), "Hi my name is \\${name} Connor", "Hi my name is ${name} Connor"},
            {new Context().withParameter("name", "John"), "Hi my name is \\\\${name} Connor", "Hi my name is \\${name} Connor"},
            {new Context().withParameter("name", "John"), "Hi my name is ${ name } Connor", "Hi my name is John Connor"},
            {new Context(), "Hi my name is ${name} Connor", "Hi my name is  Connor"},
            {new Context().withParameter("name", "John").withParameter("surname", "Connor"), "Hi my name is ${name} ${surname}", "Hi my name is John Connor"},
            {new Context(), "I have some money $30", "I have some money $30"},
            {new Context(), "I have some money $30$", "I have some money $30$"},
            {new Context(), "I have some money ${ 30", "I have some money "},
        };
    }
}
