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
import static net.mindengine.galen.specs.page.Locator.css;
import static net.mindengine.galen.specs.page.Locator.id;
import static net.mindengine.galen.specs.page.Locator.xpath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionCookie;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionWait;
import net.mindengine.galen.suite.actions.GalenPageActionWait.UntilType;

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
            {"cookie \"somecookie1\"", new GalenPageActionCookie().withCookies("somecookie1")},
            {"wait 10s", new GalenPageActionWait().withTimeout(10000)},
            {"wait 2m", new GalenPageActionWait().withTimeout(120000)},
            {"wait 10s until visible \"css: div.list\" \"xpath: //div[@id='qwe']\"", new GalenPageActionWait()
                .withTimeout(10000)
                .withUntilElements(asList(visible(css("div.list")), visible(xpath("//div[@id='qwe']"))))},
            {"wait 10s until hidden \"css: div.list\" \"xpath: //div[@id='qwe']\"", new GalenPageActionWait()
                .withTimeout(10000)
                .withUntilElements(asList(hidden(css("div.list")), hidden(xpath("//div[@id='qwe']"))))},
            {"wait 10s until gone \"id: login\" \"xpath: //div[@id='qwe']\"", new GalenPageActionWait()
                .withTimeout(10000)
                .withUntilElements(asList(gone(id("login")), gone(xpath("//div[@id='qwe']"))))},
            {"wait 10s until exist \"id: login\" gone \"xpath: //div[@id='qwe']\"", new GalenPageActionWait()
                .withTimeout(10000)
                .withUntilElements(asList(exist(id("login")), gone(xpath("//div[@id='qwe']"))))},
        };
    }
    
    private static GalenPageActionWait.Until visible(Locator locator) {
        return new GalenPageActionWait.Until(UntilType.VISIBLE, locator);
    }
    
    private static GalenPageActionWait.Until hidden(Locator locator) {
        return new GalenPageActionWait.Until(UntilType.HIDDEN, locator);
    }
    
    private static GalenPageActionWait.Until exist(Locator locator) {
        return new GalenPageActionWait.Until(UntilType.EXIST, locator);
    }
    
    private static GalenPageActionWait.Until gone(Locator locator) {
        return new GalenPageActionWait.Until(UntilType.GONE, locator);
    }
    
    //TODO negative tests for action parser
}
