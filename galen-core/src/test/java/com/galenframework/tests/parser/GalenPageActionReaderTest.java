/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.parser;

import static java.util.Arrays.asList;
import static com.galenframework.specs.page.Locator.css;
import static com.galenframework.specs.page.Locator.id;
import static com.galenframework.specs.page.Locator.xpath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.galenframework.suite.actions.*;
import com.galenframework.parser.GalenPageActionReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.actions.GalenPageActionWait.UntilType;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalenPageActionReaderTest {


    private static final List<String> EMPTY_TAGS = Collections.emptyList();
    private static final Map<String, Object> EMPTY_VARIABLES = Collections.emptyMap();

    @Test(dataProvider="provideGoodSamples") public void shouldParse_action_successfully(String actionText, GalenPageAction expectedAction) {
        GalenPageAction realAction = GalenPageActionReader.readFrom(actionText, null);
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
            
            {"check page1.spec", new GalenPageActionCheck()
                    .withSpec("page1.spec")
                    .withIncludedTags(EMPTY_TAGS)
                    .withExcludedTags(EMPTY_TAGS)
                    .withJsVariables(EMPTY_VARIABLES)},
            {"check page1.spec --include mobile --exclude debug", new GalenPageActionCheck()
                .withSpec("page1.spec")
                .withIncludedTags(asList("mobile"))
                .withExcludedTags(asList("debug"))
                .withJsVariables(EMPTY_VARIABLES)},
            {"check page1.spec --include mobile,tablet --exclude nomobile,debug", new GalenPageActionCheck()
                .withSpec("page1.spec")
                .withIncludedTags(asList("mobile", "tablet"))
                .withExcludedTags(asList("nomobile", "debug"))
                .withJsVariables(EMPTY_VARIABLES)},
            {"check page1.spec --section \"Some section * filter\"", new GalenPageActionCheck()
                .withSpec("page1.spec")
                .withIncludedTags(EMPTY_TAGS)
                .withExcludedTags(EMPTY_TAGS)
                .withSectionNameFilter("Some section * filter")
                .withJsVariables(EMPTY_VARIABLES)},
            {"check page1.spec --VuserName John", new GalenPageActionCheck()
                .withSpec("page1.spec")
                .withIncludedTags(EMPTY_TAGS)
                .withExcludedTags(EMPTY_TAGS)
                .withJsVariables(new HashMap<String, Object>(){{
                    put("userName", "John");
                }})
            },
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
            {"properties \"some-path-1/file.properties\" file2.properties", new GalenPageActionProperties()
                .withFiles(asList("some-path-1/file.properties", "file2.properties"))
            },
            {"dump page1.spec --name \"Home page dump\" --export /export/dir/path", new GalenPageActionDumpPage()
                .withSpecPath("page1.spec").withPageName("Home page dump").withPageDumpPath("/export/dir/path")
            },
            {"dump page1.spec --name \"Home page dump\" --export /export/dir/path --max-width 120 --max-height 240", new GalenPageActionDumpPage()
                .withSpecPath("page1.spec").withPageName("Home page dump").withPageDumpPath("/export/dir/path").withMaxWidth(120).withMaxHeight(240).withOnlyImages(false)
            },
            {"dump page1.spec --name \"Home page dump\" --export /export/dir/path --only-images --max-width 120 --max-height 240", new GalenPageActionDumpPage()
                .withSpecPath("page1.spec").withPageName("Home page dump").withPageDumpPath("/export/dir/path").withMaxWidth(120).withMaxHeight(240).withOnlyImages(true)
            }
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
}
