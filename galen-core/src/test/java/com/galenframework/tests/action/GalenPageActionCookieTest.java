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
package com.galenframework.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import com.galenframework.components.MockedBrowser;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionCookie;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.reports.TestReport;

import org.testng.annotations.Test;

public class GalenPageActionCookieTest {

    @Test public void shouldSetCookie_andRefreshPage() throws Exception {
        MockedBrowser browser = new MockedBrowser("", new Dimension(1024, 768), new MockedPage());
        
        GalenPageActionCookie action = new GalenPageActionCookie();
        
        action.setCookies(Arrays.asList("cookieName1=cookieValue1; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/", "cookieName2=cookieValue2; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/"));
        action.execute(new TestReport(), browser, new GalenPageTest(), null);
        
        List<String> recordedActions = browser.getRecordedActions();
        
        assertThat(recordedActions, contains("executeJavascript\ndocument.cookie=\"cookieName1=cookieValue1; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/\";" +
        		                                                "document.cookie=\"cookieName2=cookieValue2; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/\";",
        		                              "refresh"));
    }
}
