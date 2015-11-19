/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.GalenMain;
import com.galenframework.components.DummyCompleteListener;
import com.galenframework.components.JsTestRegistry;
import com.galenframework.config.GalenProperty;
import com.galenframework.runner.CompleteListener;
import com.galenframework.suite.reader.GalenSuiteReader;
import com.galenframework.tests.GalenTest;
import com.galenframework.config.GalenConfig;
import com.galenframework.specs.Spec;

import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

@Test(singleThreaded = true)
public class GalenMainTest {
    @BeforeMethod
    public void initGalenProperties() {
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_USE_FAIL_EXIT_CODE, "false");
    }


    @AfterMethod
    public void resetConfigToDefaults() throws IOException {
        GalenConfig.getConfig().reset();
    }


    @Test
    public void shouldRun_javascriptTest_andGenerateReports() throws Exception {
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        String jsonReportPath = reportsDir.getAbsolutePath() + "/json-reports";


        JsTestRegistry.get().clear();

        new GalenMain().execute(new String[]{"test",
                getClass().getResource("/js-tests/simple-with-error.test.js").getFile(),
                "--htmlreport", htmlReportPath,
                "--testngreport", testngReportPath,
                "--jsonreport", jsonReportPath
        });

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

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/with-events.test.js").getFile()
        });

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

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/test-without-galen-suffix.js").getFile()
        });

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test #1 was invoked"));
    }

    @Test
    public void shouldFind_javascriptTests_basedOnConfigProperty() throws Exception {
        JsTestRegistry.get().clear();

        GalenConfig.getConfig().setProperty(GalenProperty.TEST_JS_SUFFIX, ".blahblah.js");

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/tests-with-custom-suffix").getFile()
        });

        assertThat(JsTestRegistry.get().getEvents(), containsInAnyOrder(
                "Test #1 was invoked",
                "Test #2 was invoked"
        ));

    }


    @Test 
    public void shouldRunJavascriptTests_andFilterThem() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/testfilter.test.js").getFile()
        });

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test D invoked",
                "Test C invoked",
                "Test A invoked"
                ));
    }


    @Test
    public void shouldRunJavascriptTests_onlyForSpecifiedGroups_withTwoGroups() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new String[]{
                        "test",
                        getClass().getResource("/js-tests/testgroups.test.js").getFile(),
                        "--groups", "mobile,tablet"
        });

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test A invoked",
                "Test B invoked",
                "Test C invoked"
        ));
    }

    @Test
    public void shouldRunJavascriptTests_onlyForSpecifiedGroups_withOneGroup() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new String[] {
                        "test",
                        getClass().getResource("/js-tests/testgroups.test.js").getFile(),
                        "--groups", "tablet"
        });

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test B invoked",
                "Test C invoked"
        ));
    }

    @Test
    public void shouldRunJavascriptTests_withExcludedGroups() throws Exception {
        JsTestRegistry.get().clear();

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/testgroups.test.js").getFile(),
                "--excluded-groups", "tablet"
        });

        assertThat(JsTestRegistry.get().getEvents(), contains(
                "Test A invoked",
                "Test D invoked"
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

        new GalenMain().execute(new String[]{
                "test",
                getClass().getResource("/js-tests/testretry.test.js").getFile(),
                "--htmlreport", htmlReportDir.getAbsolutePath()
        });

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
    public void shouldGenerate_configFile() throws Exception {
        new GalenMain().execute(new String[]{"config"});
        assertThat("config file should exist", new File("galen.config").exists(), is(true));
        new File("galen.config").delete();
    }
    
    @Test 
    public void shouldNot_overrideExistingConfigFile() throws IOException {
        File file = new File("galen.config");
        file.createNewFile();
        FileUtils.writeStringToFile(file, "someTestDate = qwertyuiop");
        
        new GalenMain().execute(new String[]{"config"});

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
        
        galen.execute(new String[]{
                "test", testUrl,
                "--filter", "*with filter*"
        });
        
        assertThat("Amount of executed tests should be", executedSuites.size(), is(3));
        assertThat(executedSuites, hasItems(
                "Test 1 with filter one", 
                "Test 1 with filter two",
                "Test 2 with filter"));
    }

    @Test
    public void shouldCheckLayout_inJsTests_andPassCustomJsVariables() throws Exception {
        String testUrl = getClass().getResource("/suites/custom-js-variables-for-checklayout/simple.test.js").getFile();
        GalenMain galen = new GalenMain();
        final List<String> errorMessages = new LinkedList<>();

        CompleteListener listener = new DummyCompleteListener() {
            @Override
            public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
                errorMessages.addAll(result.getError().getMessages());
            }
        };
        galen.setListener(listener);
        galen.execute(new String[]{
                "test", testUrl
        });

        assertThat(errorMessages, hasItems("\"caption\" text is \"Hi my name is John\" but should be \"Hi my name is Jack\""));
    }

    @Test
    public void shouldPrintHelp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new GalenMain(ps, System.err).execute(new String[]{"help"});

        String realText = baos.toString("UTF-8");
        assertThat(realText, allOf(containsString("Galen Framework is an open-source tool for testing layout"),
                containsString("Apache License")));
    }
}

