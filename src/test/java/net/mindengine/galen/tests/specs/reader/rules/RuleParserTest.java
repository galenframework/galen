/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.specs.reader.rules;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.page.rules.Rule;
import net.mindengine.galen.specs.reader.page.rules.RuleParser;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by ishubin on 2015/02/21.
 */
public class RuleParserTest {

    @Test
    public void shouldParse_basicRule_andTrimIt() {
        Rule rule = new RuleParser().parse(" \tShould be squared ");

        Pattern rulePattern = rule.getPattern();
        assertThat(rulePattern.pattern(), is("\\QShould be squared\\E"));
        assertThat(rule.getParameters(), is(emptyCollectionOf(String.class)));
    }


    @Test
    public void shouldParse_ruleWithParameters_withDefaultRegex_andTrimmedParameters() {
        Rule rule = new RuleParser().parse("Should be placed near %{ secondObject } with %{  margin } % margin");

        Pattern rulePattern = rule.getPattern();
        assertThat(rulePattern.pattern(), is("\\QShould be placed near \\E(.*)\\Q with \\E(.*)\\Q % margin\\E"));
        assertThat(rule.getParameters(), contains("secondObject", "margin"));
    }

    @Test
    public void shouldParse_ruleWithParameters_withCustomAndTrimmedRegex() {
        Rule rule = new RuleParser().parse("Should be placed near %{secondObject: menu-item-.*  } with %{margin: \\d{3}:} margin");

        Pattern rulePattern = rule.getPattern();
        assertThat(rulePattern.pattern(), is("\\QShould be placed near \\E(menu-item-.*)\\Q with \\E(\\d{3}:)\\Q margin\\E"));
        assertThat(rule.getParameters(), contains("secondObject", "margin"));
    }


    @Test(dataProvider = "negativeTests")
    public void shouldThrowError_whenParsing_incorrectRule(String ruleText, String expectedMessage) {
        try {
            new RuleParser().parse(ruleText);

            throw new RuntimeException("It should throw an error previously but didn't");
        }
        catch (SyntaxException ex) {
            assertThat(ex.getMessage(), is(expectedMessage));
        }
    }


    @DataProvider
    public Object[][] negativeTests() {
        return new Object[][]{
                {"Hi %{faasfsaF{}asf", "Missing '}' to close parameter definition"},
                {"Hi %{objectName .*}", "Incorrect parameter name: objectName .*"},
                {"Hi %{ :.*}", "Parameter name should not be empty"},
                {"Hi %{someParameter: }", "Missing custom regular expression after ':'"}
        };

    }

}
