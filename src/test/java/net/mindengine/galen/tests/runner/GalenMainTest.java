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
package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.GalenMain;
import net.mindengine.galen.components.DummyCompleteListener;
import net.mindengine.galen.components.JsTestRegistry;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenArguments;
import net.mindengine.galen.tests.GalenTest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.google.common.io.Files;

@Test(singleThreaded = true)
public class GalenMainTest {


    @Test
    public void shouldRun_javascriptTest_andGenerateReports() throws Exception {
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        String jsonReportPath = reportsDir.getAbsolutePath() + "/json-reports";


        JsTestRegistry.get().clear();
        
        new GalenMain().execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/js-tests/simple-with-error.test.js").getFile()))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
            .withJsonReport(jsonReportPath)
        );
        
        assertThat(JsTestRegistry.get().getEvents().size(), is(3));
        assertThat(JsTestRegistry.get().getEvents().get(0), is("Test #1 was invoked"));
        assertThat(JsTestRegistry.get().getEvents().get(1), is("Test #2 was invoked"));
        assertThat(JsTestRegistry.get().getEvents().get(2), is("Test #3 was invoked"));
        
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
                
        assertThat(testngReportContent, containsString("<test name=\"Test number 1\">"));
        assertThat(testngReportContent, containsString("<class name=\"Test number 1\">"));
        
        assertThat(testngReportContent, containsString("<test name=\"Test number 2\">"));
        assertThat(testngReportContent, containsString("<class name=\"Test number 2\">"));
        
        assertThat(testngReportContent, containsString("<test name=\"Test number 3\">"));
        assertThat(testngReportContent, containsString("<class name=\"Test number 3\">"));
        
        
        String htmlReportContent = FileUtils.readFileToString(new File(htmlReportPath + File.separator + "report.html"));
        
        assertThat(htmlReportContent, containsString("\"testId\" : \"1-test-number-1\""));
        assertThat(htmlReportContent, containsString("\"testId\" : \"2-test-number-2\""));
        assertThat(htmlReportContent, containsString("\"testId\" : \"3-test-number-3\""));



        File jsonReportDir = new File(jsonReportPath);
        assertThat("json-reports folder should be created", jsonReportDir.exists() && jsonReportDir.isDirectory(), is(true));

        assertThat("json-reports folder contains files", asList(jsonReportDir.list()), containsInAnyOrder(
                "1-test-number-1.json",
                "2-test-number-2.json",
                "3-test-number-3.json",
                "report.json"));
    }
    
    @Test 
    public void shouldRun_javascriptTestWithEvents() throws Exception {
        JsTestRegistry.get().clear();
        
        new GalenMain().execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/js-tests/with-events.test.js").getFile()))
        );
        
        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Before test suite",
                "Before test: Test number 1",
                "Test #1 was invoked",
                "After test: Test number 1",
                "Before test: Test number 2",
                "Test #2 was invoked",
                "After test: Test number 2",
                "After test suite"));
    }


    @Test
    public void shouldRun_javascriptTest_regardlessOfItsSuffix() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new GalenArguments()
                        .withAction("test")
                        .withPaths(asList(getClass().getResource("/js-tests/test-without-galen-suffix.js").getFile()))
        );

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test #1 was invoked"));
    }

    @Test 
    public void shouldRunJavascriptTests_andFilterThem() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new GalenArguments()
                        .withAction("test")
                        .withPaths(asList(getClass().getResource("/js-tests/testfilter.test.js").getFile()))
        );

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test D invoked",
                "Test C invoked",
                "Test A invoked"
                ));
    }


    @Test
    public void shouldRunJavascriptTests_onlyForSpecifiedGroups_withTwoGroups() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new GalenArguments()
                        .withAction("test")
                        .withPaths(asList(getClass().getResource("/js-tests/testgroups.test.js").getFile()))
                        .withGroups(asList("mobile", "tablet"))
        );

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test A invoked",
                "Test B invoked",
                "Test C invoked"
        ));
    }

    @Test
    public void shouldRunJavascriptTests_onlyForSpecifiedGroups_withOneGroup() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new GalenArguments()
                        .withAction("test")
                        .withPaths(asList(getClass().getResource("/js-tests/testgroups.test.js").getFile()))
                        .withGroups(asList("tablet"))
        );

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test B invoked",
                "Test C invoked"
        ));
    }

    /**
     * Comes from https://github.com/galenframework/galen/issues/184
     * Test Retry Handler
     * @throws Exception
     */
    @Test
    public void shouldRunJavascriptTests_andRetryThem() throws Exception {
        File htmlReportDir = Files.createTempDir();
        JsTestRegistry.get().clear();

        new GalenMain().execute(new GalenArguments()
                        .withAction("test")
                        .withPaths(asList(getClass().getResource("/js-tests/testretry.test.js").getFile()))
                        .withHtmlReport(htmlReportDir.getAbsolutePath())
        );

        List<String> events = JsTestRegistry.get().getEvents();


        assertThat(events, contains(
                "Before test suite event",
                "Before test event for: Test A",
                "Test A invoked",
                "After test event for: Test A",
                "Retry handler invoked for test: Test A",
                "Before test event for: Test A",
                "Test A invoked",
                "After test event for: Test A",
                "Retry handler invoked for test: Test A",
                "Before test event for: Test A",
                "Test A invoked",
                "After test event for: Test A",
                "Retry handler invoked for test: Test A",
                "Before test event for: Test B",
                "Test B invoked",
                "After test event for: Test B",
                "Retry handler invoked for test: Test B",
                "Before test event for: Test B",
                "Test B invoked",
                "After test event for: Test B",
                "Retry handler invoked for test: Test B",
                "Before test event for: Test B",
                "Test B invoked",
                "After test event for: Test B",
                "Retry handler invoked for test: Test B",
                "Before test event for: Test C",
                "Test C invoked",
                "After test event for: Test C",
                "After test suite event"
        ));


        String htmlReportContent = FileUtils.readFileToString(new File(htmlReportDir.getAbsolutePath()
                + File.separator + "report.html"));

        int amountOfReportedTests = StringUtils.countMatches(htmlReportContent, "\"testId\"");
        assertThat("Amount of reported tests should be", amountOfReportedTests, is(3));
    }
    
    @Test 
    public void shouldGenerate_configFile() throws IOException {
        new GalenMain().performConfig();
        assertThat("config file should exist", new File("config").exists(), is(true));
        new File("config").delete();
    }
    
    @Test 
    public void shouldNot_overrideExistingConfigFile() throws IOException {
        File file = new File("config");
        file.createNewFile();
        FileUtils.writeStringToFile(file, "someTestDate = qwertyuiop");
        
        new GalenMain().performConfig();
        
        String data = FileUtils.readFileToString(file);
        assertThat(data, is("someTestDate = qwertyuiop"));
        
        file.delete();
    }
    
    @Test 
    public void shouldRun_filteredTestInSuite() throws Exception {
        String testUrl = getClass().getResource("/suites/suite-for-filtering.test").getFile();
        GalenMain galen = new GalenMain();
        
        final List<String> executedSuites = new LinkedList<String>();
        
        CompleteListener listener = new DummyCompleteListener() {
            @Override
            public void onTestStarted(GalenTest test) {
                executedSuites.add(test.getName());
            }
        };
        galen.setListener(listener);
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(testUrl))
            .withFilter("*with filter*")
        );
        
        assertThat("Amount of executed tests should be", executedSuites.size(), is(3));
        assertThat(executedSuites, hasItems(
                "Test 1 with filter one", 
                "Test 1 with filter two",
                "Test 2 with filter"));
    }
}

