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
package com.galenframework.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.util.Arrays;

import com.galenframework.validation.ValidationListener;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionProperties;
import com.galenframework.tests.TestSession;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;


public class GalenPageActionPropertiesTest {
    private static final Browser NO_BROWSER = null;
    private static final ValidationListener NO_LISTENER = null;

    @Test public void shouldLoadProperties_fromSpecifiedFiles() throws Exception {
        TestSession.register(null);
        
        System.getProperties().remove("page.title");
        System.getProperties().remove("page.download.caption");
        System.getProperties().remove("login.link") ;
        GalenPageActionProperties action = new GalenPageActionProperties();
        action.setFiles(Arrays.asList(findResource("/properties/homepage-en.properties"), findResource("/properties/loginpage-en.properties")));
        
        
        action.execute(new TestReport(), NO_BROWSER, new GalenPageTest(), NO_LISTENER);
        
        assertThat("System property page.title should be", TestSession.current().getProperties().get("page.title"), Matchers.is("Home page"));
        assertThat("System property page.download.caption should be", TestSession.current().getProperties().get("page.download.caption"), Matchers.is("Take it!"));
        assertThat("System property login.link should be", TestSession.current().getProperties().get("login.link"), Matchers.is("Sign in"));
        
        TestSession.clear();
    }
    
    @Test(expectedExceptions=FileNotFoundException.class,
            expectedExceptionsMessageRegExp="File does not exist: some-unexistent-file.properties")
    public void shouldGiveError_whenFile_isNotFound() throws Exception {
        GalenPageActionProperties action = new GalenPageActionProperties();
        action.setFiles(Arrays.asList("some-unexistent-file.properties"));
        action.execute(new TestReport(), NO_BROWSER, new GalenPageTest(), NO_LISTENER);
    }

    private String findResource(String resourceName) {
        return getClass().getResource(resourceName).getFile();
    }

}
