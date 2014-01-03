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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionCookie;
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
            {"cookie \"somecookie1\" \"somecookie2\" \"somecookie3\"", new GalenPageActionCookie().withCookies("somecookie1", "somecookie2", "somecookie3")},
            {"cookie \"somecookie1\"", new GalenPageActionCookie().withCookies("somecookie1")}
        };
    }
    
    
    //TODO negative tests for action parser
}
