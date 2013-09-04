package net.mindengine.galen.tests.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GalenPageActionReaderTest {

    
    @Test(dataProvider="provideGoodSamples") public void shouldParse_action_successfully(String actionText, GalenPageAction expectedAction) {
        GalenPageAction realAction = GalenPageActionReader.readFrom(actionText);
        assertThat(realAction, is(expectedAction));
    }
    
    @DataProvider public Object[][] provideGoodSamples() {
        return new Object[][]{
            {"inject javascript.js", new GalenPageActionInjectJavascript("javascript.js")},
            {"inject   /usr/bin/john/scripts/javascript.js", new GalenPageActionInjectJavascript("/usr/bin/john/scripts/javascript.js")},
            {"inject   \"/usr/bin/john/scripts/javascript.js\"", new GalenPageActionInjectJavascript("/usr/bin/john/scripts/javascript.js")},
            
            {"run script.js \"{name: 'john'}\"", new GalenPageActionRunJavascript("script.js").withJsonArguments("{name: 'john'}")},
            {"run script.js \"\"", new GalenPageActionRunJavascript("script.js").withJsonArguments("")},
            {"run script.js \"\\\"john\\\"", new GalenPageActionRunJavascript("script.js").withJsonArguments("\"john\"")},
            {"run script.js", new GalenPageActionRunJavascript("script.js").withJsonArguments(null)},
            
            {"check page1.spec", new GalenPageActionCheck().withSpecs(asList("page1.spec"))},
            {"check page1.spec --include mobile --exclude debug", new GalenPageActionCheck()
                .withSpecs(asList("page1.spec"))
                .withIncludedTags(asList("mobile"))
                .withExcludedTags(asList("debug"))},
            {"check page1.spec --include mobile,tablet --exclude nomobile,debug", new GalenPageActionCheck()
                .withSpecs(asList("page1.spec"))
                .withIncludedTags(asList("mobile", "tablet"))
                .withExcludedTags(asList("nomobile", "debug"))},
            {"check page1.spec page2.spec page3.spec --include mobile,tablet --exclude nomobile,debug", new GalenPageActionCheck()
                .withSpecs(asList("page1.spec", "page2.spec", "page3.spec"))
                .withIncludedTags(asList("mobile", "tablet"))
                .withExcludedTags(asList("nomobile", "debug"))},
        };
    }
    
    
    //TODO negative tests for action parser
}
