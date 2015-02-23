package net.mindengine.galen.tests.specs.reader;

import net.mindengine.galen.components.specs.ExpectedSpecObject;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by ishubin on 2015/02/22.
 */
public class PageSpecsWithRulesReaderTest {

    private static final Properties EMPTY_PROPERTIES = new Properties();
    private static final Page EMPTY_PAGE = null;

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-objects-and-specs-before.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }


    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredAfterUsage_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-objects-and-specs-after.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-only-specs-before.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredAfterUsage_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-only-specs-after.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }


    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-object-and-specs.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("height: 100px")));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-only-specs.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("height: 100px")));
    }

    @Test
    public void shouldParsePageSpec_withParameterizedRule_containingOnlySpecs_andReusingObjectName_asParameter() throws IOException {
        PageSpec pageSpec = readPageSpec("parameterized-rule-only-specs-with-objectname-parameter.spec");

        PageSection ruleSection = pageSpec.getSections().get(0).getSections().get(0);

        assertThat(ruleSection.getName(), is("Login button should be visible"));
        assertThat(ExpectedSpecObject.convertSection(ruleSection),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("height: 100px")));
    }


    @Test
    public void shouldThrowError_whenRuleIsNotMatched() throws IOException {
        assertThatSyntaxExceptionIsThrownFor("error-rule-not-matched.spec",
                "There are no rules that matching this: | Login button should ve invisible");
    }

    private void assertThatSyntaxExceptionIsThrownFor(String pageSpecPath, String expectedMessage) throws IOException {
        try {
            readPageSpec("parameterized-rule-only-specs-with-objectname-parameter.spec");
            throw new RuntimeException("The expected exception was not thrown");
        } catch (SyntaxException ex) {
            assertThat(ex.getMessage(), is(expectedMessage));
        }

    }

    private PageSpec readPageSpec(String specName) throws IOException {
        return new PageSpecReader(EMPTY_PROPERTIES, EMPTY_PAGE).read(getClass().getResourceAsStream("/page-spec-with-rules/" + specName));
    }

}
