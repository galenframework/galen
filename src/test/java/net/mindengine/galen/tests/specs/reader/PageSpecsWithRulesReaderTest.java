package net.mindengine.galen.tests.specs.reader;

import net.mindengine.galen.components.specs.ExpectedSpecObject;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Created by ishubin on 2015/02/22.
 */
public class PageSpecsWithRulesReaderTest {

    private static final Properties EMPTY_PROPERTIES = new Properties();
    private static final Page EMPTY_PAGE = null;

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-objects-and-specs-before.spec");

        assertThat(ExpectedSpecObject.convertSection(pageSpec.getSections().get(0)),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }


    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredAfterUsage_containingObjectAndSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-objects-and-specs-after.spec");

        assertThat(ExpectedSpecObject.convertSection(pageSpec.getSections().get(0)),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredBeforeUsage_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-only-specs-before.spec");

        assertThat(ExpectedSpecObject.convertSection(pageSpec.getSections().get(0)),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    @Test
    public void shouldParsePageSpec_withSimpleRule_declaredAfterUsage_containingOnlySpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("simple-rule-only-specs-after.spec");

        assertThat(ExpectedSpecObject.convertSection(pageSpec.getSections().get(0)),
                contains(new ExpectedSpecObject("login-button")
                        .withSpecs("visible")));
    }

    private PageSpec readPageSpec(String specName) throws IOException {
        return new PageSpecReader(EMPTY_PROPERTIES, EMPTY_PAGE).read(getClass().getResourceAsStream("/page-spec-with-rules/" + specName));
    }

}
