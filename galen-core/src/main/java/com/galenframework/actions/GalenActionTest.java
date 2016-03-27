/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.actions;

import com.galenframework.TestRunnable;
import com.galenframework.config.GalenConfig;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.JunitReportBuilder;
import com.galenframework.reports.TestNgReportBuilder;
import com.galenframework.reports.json.JsonReportBuilder;
import com.galenframework.reports.model.FileTempStorage;
import com.galenframework.runner.CombinedListener;
import com.galenframework.runner.CompleteListener;
import com.galenframework.runner.EventHandler;
import com.galenframework.runner.JsTestCollector;
import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.suite.reader.GalenSuiteReader;
import com.galenframework.tests.GalenTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class GalenActionTest extends GalenAction {
    private final static Logger LOG = LoggerFactory.getLogger(GalenActionTest.class);

    private final GalenActionTestArguments testArguments;
    private final CombinedListener listener;

    public GalenActionTest(String[] arguments, PrintStream outStream, PrintStream errStream, CombinedListener listener) {
        super(arguments, outStream, errStream);
        this.testArguments = GalenActionTestArguments.parse(arguments);
        this.listener = createListeners(listener);
    }

    @Override
    public void execute() throws Exception {
        loadConfigIfNeeded(getTestArguments().getConfig());

        List<File> basicTestFiles = new LinkedList<>();
        List<File> jsTestFiles = new LinkedList<>();

        for (String path : testArguments.getPaths()) {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    searchForTests(file, testArguments.getRecursive(), basicTestFiles, jsTestFiles);
                } else if (file.isFile()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(GalenConfig.getConfig().getTestSuffix())) {
                        basicTestFiles.add(file);
                    } else if (name.endsWith(".js")) {
                        jsTestFiles.add(file);
                    }
                }
            } else {
                throw new FileNotFoundException(path);
            }
        }

        if (basicTestFiles.size() > 0 || jsTestFiles.size() > 0) {
            runTestFiles(basicTestFiles, jsTestFiles);
        } else {
            throw new RuntimeException("Couldn't find any test files");
        }
    }

    private void runTestFiles(List<File> basicTestFiles, List<File> jsTestFiles) throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();

        List<GalenTest> tests = new LinkedList<>();
        for (File file : basicTestFiles) {
            tests.addAll(reader.read(file));
        }

        JsTestCollector testCollector = new JsTestCollector(tests);
        for (File jsFile : jsTestFiles) {
            testCollector.execute(jsFile);
        }

        testCollector.getEventHandler().invokeBeforeTestSuiteEvents();

        runTests(testCollector.getEventHandler(), tests, testArguments, listener);

        testCollector.getEventHandler().invokeAfterTestSuiteEvents();
    }

    public static void runTests(EventHandler eventHandler, List<GalenTest> tests, GalenActionTestArguments testArguments, CombinedListener listener) {
        if (testArguments.getParallelThreads() > 1) {
            runTestsInThreads(eventHandler, tests, testArguments.getParallelThreads(), testArguments, listener);
        } else {
            runTestsInThreads(eventHandler, tests, 1, testArguments, listener);
        }
    }

    private static void runTestsInThreads(final EventHandler eventHandler, List<GalenTest> tests,
                                          int amountOfThreads, GalenActionTestArguments testArguments, CombinedListener listener) {
        ExecutorService executor = Executors.newFixedThreadPool(amountOfThreads);

        Pattern filterPattern = createTestFilter(testArguments.getFilter());

        List<GalenTest> filteredTests = filterTests(tests, eventHandler);

        tellBeforeTestSuite(listener, filteredTests);

        List<GalenTestInfo> testInfos = Collections.synchronizedList(new LinkedList<GalenTestInfo>());

        for (final GalenTest test : filteredTests) {
            if (matchesPattern(test.getName(), filterPattern)
                    && matchesSelectedGroups(test, testArguments.getGroups())
                    && doesNotMatchExcludedGroups(test, testArguments.getExcludedGroups())) {
                executor.execute(new TestRunnable(test, listener, eventHandler, testInfos));
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        tellAfterTestSuite(testInfos, listener);

        createAllReports(testInfos, testArguments);

        cleanData(testInfos);
    }

    private void searchForTests(File file, boolean recursive, List<File> files, List<File> jsFiles, int level) {

        String fileName = file.getName().toLowerCase();
        if (file.isFile()) {
            if (fileName.endsWith(GalenConfig.getConfig().getTestSuffix())) {
                files.add(file);
            } else if (fileName.endsWith(GalenConfig.getConfig().getTestJsSuffix())) {
                jsFiles.add(file);
            }
        } else if (file.isDirectory() && (level == 0 || recursive)) {
            for (File childFile : file.listFiles()) {
                searchForTests(childFile, recursive, files, jsFiles, level + 1);
            }
        }
    }

    private static void cleanData(List<GalenTestInfo> testInfos) {
        for (GalenTestInfo testInfo : testInfos) {
            if (testInfo.getReport() != null) {
                FileTempStorage storage = testInfo.getReport().getFileStorage();
                if (storage != null) {
                    storage.cleanup();
                }
            }
        }
    }

    private static boolean doesNotMatchExcludedGroups(GalenTest test, List<String> excludedGroups) {
        if (excludedGroups != null && excludedGroups.size() > 0) {
            return !matchesSelectedGroups(test, excludedGroups);
        }
        return true;
    }

    private static boolean matchesSelectedGroups(GalenTest test, List<String> selectedGroups) {
        if (selectedGroups != null && selectedGroups.size() > 0) {
            List<String> testGroups = test.getGroups();

            if (testGroups != null && testGroups.size() > 0) {
                for (String testGroup : testGroups) {
                    if (selectedGroups.contains(testGroup)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private static List<GalenTest> filterTests(List<GalenTest> tests, EventHandler eventHandler) {
        List<TestFilterEvent> filters = eventHandler.getTestFilterEvents();
        if (filters != null && filters.size() > 0) {
            GalenTest[] arrTests = tests.toArray(new GalenTest[]{});

            for (TestFilterEvent filter : filters) {
                arrTests = filter.execute(arrTests);
            }

            if (arrTests == null) {
                arrTests = new GalenTest[]{};
            }

            return asList(arrTests);
        } else {
            return tests;
        }
    }

    private static void tellBeforeTestSuite(CompleteListener listener, List<GalenTest> tests) {
        if (listener != null) {
            try {
                listener.beforeTestSuite(tests);
            } catch (Exception ex) {
                LOG.error("Unknow error before running testsuites.", ex);
            }
        }
    }

    private static void tellAfterTestSuite(List<GalenTestInfo> testInfos, CombinedListener listener) {
        if (listener != null) {
            try {
                listener.afterTestSuite(testInfos);
            } catch (Exception ex) {
                LOG.error("Unknow error after running testsuites.", ex);
            }
        }
    }

    private static void createAllReports(List<GalenTestInfo> testInfos, GalenActionTestArguments testArguments) {
        if (testArguments.getTestngReport() != null) {
            createTestngReport(testArguments.getTestngReport(), testInfos);
        }
        if (testArguments.getJunitReport() != null) {
            createJunitReport(testArguments.getJunitReport(), testInfos);
        }
        if (testArguments.getHtmlReport() != null) {
            createHtmlReport(testArguments.getHtmlReport(), testInfos);
        }
        if (testArguments.getJsonReport() != null) {
            createJsonReport(testArguments.getJsonReport(), testInfos);
        }
    }

    private static void createJsonReport(String jsonReport, List<GalenTestInfo> testInfos) {
        try {
            new JsonReportBuilder().build(testInfos, jsonReport);
        } catch (IOException e) {
            LOG.error("Failed generating json report", e);
        }
    }

    private static void createHtmlReport(String htmlReportPath, List<GalenTestInfo> testInfos) {
        try {
            new HtmlReportBuilder().build(testInfos, htmlReportPath);
        } catch (Exception ex) {
            LOG.error("Unknown error during creating HTML report.", ex);
        }
    }

    private static void createJunitReport(String junitReport, List<GalenTestInfo> testInfos) {
        try {
            new JunitReportBuilder().build(testInfos, junitReport);
        } catch (Exception ex) {
            LOG.error("Unknown error during creating Junit report.", ex);
        }
    }

    private static void createTestngReport(String testngReport, List<GalenTestInfo> testInfos) {
        try {
            new TestNgReportBuilder().build(testInfos, testngReport);
        } catch (Exception ex) {
            LOG.error("Unknown error during creating TestNG report.", ex);
        }
    }

    private static boolean matchesPattern(String name, Pattern filterPattern) {
        if (filterPattern != null) {
            return filterPattern.matcher(name).matches();
        } else
            return true;
    }

    private static Pattern createTestFilter(String filter) {
        return filter != null ? Pattern.compile(filter.replace("*", ".*")) : null;
    }

    private void searchForTests(File file, boolean recursive, List<File> files, List<File> jsFiles) {
        searchForTests(file, recursive, files, jsFiles, 0);
    }

    public GalenActionTestArguments getTestArguments() {
        return testArguments;
    }
}
