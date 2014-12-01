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
package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.GalenMain;
import net.mindengine.galen.components.DummyCompleteListener;
import net.mindengine.galen.components.JsTestRegistry;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenArguments;
import net.mindengine.galen.tests.GalenTest;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.google.common.io.Files;


public class GalenMainTest {

    @Test public void shouldRun_singleTestSuccessfully() throws Exception {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        System.setProperty("url", testUrl);
        System.setProperty("spec.path", getClass().getResource("/html/page.spec").getFile());
        
        GalenMain galen = new GalenMain();
        
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/suites/to-run/suite-single.test").getFile()))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
            );
        
        assertThat("Should create screenshot 1 and place it in same folder as report", new File(reportsDir.getAbsolutePath() + "/report-1-home-page-test-screenshot-1.png").exists(), is(true));
        assertThat("Should create screenshot 2 and place it in same folder as report", new File(reportsDir.getAbsolutePath() + "/report-2-home-page-test-2-screenshot-2.png").exists(), is(true));
        
        String htmlReportContent = FileUtils.readFileToString(new File(htmlReportPath + File.separator + "report.html"));
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        //Verifying only parts of the report content to make sure that the test were executed
        assertThat(htmlReportContent, containsString("<a href=\"report-1-home-page-test.html\">Home page test</a>"));
        assertThat(htmlReportContent, containsString("<a href=\"report-2-home-page-test-2.html\">Home page test 2</a>"));
        
        assertThat(testngReportContent, containsString("<test name=\"Home page test\">"));
        assertThat(testngReportContent, containsString("<class name=\"Home page test\">"));
        
        assertThat(testngReportContent, containsString("<test name=\"Home page test 2\">"));
        assertThat(testngReportContent, containsString("<class name=\"Home page test 2\">"));
    }
    
    @Test public void shouldFindAndRun_allTestsRecursivelly() throws Exception {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        System.setProperty("url", testUrl);
        System.setProperty("spec.path", getClass().getResource("/html/page.spec").getFile());
        
        GalenMain galen = new GalenMain();
        
        File reportsDir = Files.createTempDir();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.xml";
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/suites/to-run/recursive-check").getFile()))
            .withRecursive(true)
            .withTestngReport(testngReportPath)
            );
        
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        assertThat(testngReportContent, containsString("<test name=\"Recursion check 1\">"));
        assertThat(testngReportContent, containsString("<class name=\"Recursion check 1\">"));
        
        assertThat(testngReportContent, containsString("<test name=\"Recursion check 2\">"));
        assertThat(testngReportContent, containsString("<class name=\"Recursion check 2\">"));
        
        assertThat(testngReportContent, containsString("<test name=\"Recursion check 3\">"));
        assertThat(testngReportContent, containsString("<class name=\"Recursion check 3\">"));
    }
    
    @Test public void shouldRun_javascriptTest() throws Exception {
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        JsTestRegistry.get().clear();
        
        new GalenMain().execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/js-tests/simple-with-error.test.js").getFile()))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
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
        
        assertThat(htmlReportContent, containsString("<a href=\"report-1-test-number-1.html\">Test number 1</a>"));
        assertThat(htmlReportContent, containsString("<a href=\"report-2-test-number-2.html\">Test number 2</a>"));
        assertThat(htmlReportContent, containsString("<a href=\"report-3-test-number-3.html\">Test number 3</a>"));
        assertThat(htmlReportContent, containsString("<td class=\"status failed\">1</td>"));
    }
    
    @Test public void shouldRun_javascriptTestWithEvents() throws Exception {
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

    @Test public void shouldRunJavascriptTests_andFilterThem() throws Exception {
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
    
    @Test public void shouldFindAndRun_allTestsRecursivelly_inParallel() throws Exception {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        System.setProperty("url", testUrl);
        System.setProperty("spec.path", getClass().getResource("/html/page.spec").getFile());
        
        GalenMain galen = new GalenMain();
        
        File reportsDir = Files.createTempDir();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/suites/to-run/recursive-check").getFile()))
            .withRecursive(true)
            .withTestngReport(testngReportPath)
            .withParallelSuites(5)
            );
    }
    
    @Test public void shouldRun_simplePageCheck() throws Exception {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        String pageSpec = getClass().getResource("/html/page.spec").getFile();
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        new GalenMain().execute(new GalenArguments()
            .withAction("check")
            .withUrl(testUrl)
            .withPaths(Arrays.asList(pageSpec))
            .withScreenSize(new Dimension(450, 500))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
            .withIncludedTags("desktop"));
        
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        assertThat(testngReportContent, containsString("<test name=\"" + pageSpec + "\">"));
        assertThat(testngReportContent, containsString("<class name=\"" + pageSpec + "\">"));
    }
    
    @Test public void shouldGiveError_whenPageSpecIsIncorrect() throws Exception {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        String pageSpec = getClass().getResource("/negative-specs/invalid-spec.spec").getFile();
        File reportsDir = Files.createTempDir();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        new GalenMain().execute(new GalenArguments()
            .withAction("check")
            .withUrl(testUrl)
            .withPaths(Arrays.asList(pageSpec))
            .withScreenSize(new Dimension(450, 500))
            .withTestngReport(testngReportPath)
            .withIncludedTags("desktop")
            .withOriginal("check invalid-spec.spec --include desktop"));
        
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));

        assertThat(testngReportContent, containsString("<test-method status=\"FAIL\""));
    }
    
    @Test public void shouldGenerate_configFile() throws IOException {
        new GalenMain().performConfig();
        assertThat("config file should exist", new File("config").exists(), is(true));
        new File("config").delete();
    }
    
    @Test public void shouldNot_overrideExistingConfigFile() throws IOException {
        File file = new File("config");
        file.createNewFile();
        FileUtils.writeStringToFile(file, "someTestDate = qwertyuiop");
        
        new GalenMain().performConfig();
        
        String data = FileUtils.readFileToString(file);
        assertThat(data, is("someTestDate = qwertyuiop"));
        
        file.delete();
    }
    
    @Test public void shouldRun_filteredTestInSuite() throws Exception {
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

