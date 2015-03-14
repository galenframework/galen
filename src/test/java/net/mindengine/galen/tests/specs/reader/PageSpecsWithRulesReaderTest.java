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
package net.mindengine.galen.tests.specs.reader;

import net.mindengine.galen.components.specs.ExpectedSpecObject;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by ishubin on 2015/02/22.
 */
public class PageSpecsWithRulesReaderTest {

    private static final Properties EMPTY_PROPERTIES = null;
    private static final Page EMPTY_PAGE = null;

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-objects-and-specs-before.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);


        assertThat(pageSpec.getSections().get(0).getName(), is("Header"));

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredInImportedFile() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-import.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-only-specs-before.spec");

        PageSection ruleSection = pageSpec.getSections().get(0);

        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecGroup("should be visible", asList("visible"))));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-object-and-specs.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("login-button should have 100 pixels height"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("height: 100 px")));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-only-specs.spec");

        PageSection ruleSection = pageSpec.getSections().get(0);

        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecGroup("should be a square with 50 pixels size",
                                asList("width: 50 px",
                                       "height: 50 px"))));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingOnlySpecs_andReusingObjectName_asParameter() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-only-specs-with-objectname-parameter.spec");

        PageSection ruleSection = pageSpec.getSections().get(0);

        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecGroup("should be squared",
                                asList("width: 100% of login-button/height"))));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_providedFromJavaScript() throws IOException {
        PageSpec pageSpec = readPageSpec("rules-provided-via-js.spec");

        PageSection globalSection = pageSpec.getSections().get(0);
        PageSection ruleSection = globalSection.getSections().get(0);

        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(
                        new ExpectedSpecObject("login-button")
                                .withSpecs("aligned horizontally all: cancel-button")
                ));


        assertThat(ExpectedSpecObject.convertSection(globalSection),
                contains(
                        new ExpectedSpecObject("cancel-button")
                            .withSpecGroup("squared", asList("width: 100% of cancel-button/height"))
                        ));
    }


    @Test
    public void shouldThrowError_whenRuleIsNotMatched() throws IOException {
        assertThatSyntaxExceptionIsThrownFor("error-rule-not-matched.spec",
                "There are no rules matching: Login button should be visible");
    }

    private void assertThatSyntaxExceptionIsThrownFor(String pageSpecPath, String expectedMessage) throws IOException {
        try {
            readPageSpec(pageSpecPath);
            throw new RuntimeException("The expected exception was not thrown");
        } catch (FileSyntaxException ex) {
            assertThat(ex.getCause().getMessage(), is(expectedMessage));
        }

    }

    private PageSpec readPageSpec(String specName) throws IOException {
        return new PageSpecReader(null, EMPTY_PAGE).read("/page-spec-with-rules/" + specName);
    }

}
