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
package net.mindengine.galen;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.reports.ConsoleReportingListener;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.HtmlReportBuilder;
import net.mindengine.galen.reports.TestNgReportBuilder;
import net.mindengine.galen.reports.json.JsonReportBuilder;
import net.mindengine.galen.runner.CombinedListener;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.EventHandler;
import net.mindengine.galen.runner.GalenArguments;
import net.mindengine.galen.runner.JsTestCollector;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.runner.events.TestFilterEvent;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.reader.GalenSuiteReader;
import net.mindengine.galen.tests.GalenBasicTest;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.validation.FailureListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalenMain {

    private final static Logger LOG = LoggerFactory.getLogger(GalenMain.class);

    private CompleteListener listener;

    public void execute(final GalenArguments arguments) throws Exception {
        if (arguments.getAction() != null) {

            final FailureListener failureListener = new FailureListener();
            final CombinedListener combinedListener = createListeners(arguments);
            combinedListener.add(failureListener);
            if (listener != null) {
                combinedListener.add(listener);
            }

            if ("test".equals(arguments.getAction())) {
                runTests(arguments, combinedListener);
            } else if ("check".equals(arguments.getAction())) {
                performCheck(arguments, combinedListener);
            } else if ("config".equals(arguments.getAction())) {
                performConfig();
            } else if ("dump".equals(arguments.getAction())) {
                performPageDump(arguments);
            }
            combinedListener.done();

            if (GalenConfig.getConfig().getUseFailExitCode()) {
                if (failureListener.hasFailures()) {
                    System.err.println("There were failures in galen tests");
                    System.exit(1);
                }
            }
        } else {
            if (arguments.getPrintVersion()) {
                System.out.println("Galen Framework");
                String version = getClass().getPackage().getImplementationVersion();
                if (version == null) {
                    version = "unknown";
                } else {
                    version = version.replace("-SNAPSHOT", "");
                }
                System.out.println("Version: " + version);
            }
        }
    }

    private void performPageDump(final GalenArguments arguments) throws SyntaxException {
        final SeleniumBrowserFactory browserFactory = new SeleniumBrowserFactory();
        final Browser browser = browserFactory.openBrowser();

        try {

            if (StringUtils.isEmpty(arguments.getUrl())) {
                throw new SyntaxException("--url parameter is not defined");
            }
            if (CollectionUtils.isEmpty(arguments.getPaths())) {
                throw new SyntaxException("You should specify a spec file with which you want to make a page dump");
            }
            if (StringUtils.isEmpty(arguments.getExport())) {
                throw new SyntaxException("--export parameter is not defined");
            }

            if (arguments.getScreenSize() != null) {
                browser.changeWindowSize(arguments.getScreenSize());
            }

            browser.load(arguments.getUrl());

            Galen.dumpPage(browser, arguments.getUrl(), arguments.getPaths().get(0), arguments.getExport(), arguments.getMaxWidth(), arguments.getMaxHeight());
            System.out.println("Done!");
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            browser.quit();
        }
    }

    public void performConfig() throws IOException {
        final File file = new File("config");

        if (!file.exists()) {
            file.createNewFile();
            final FileOutputStream fos = new FileOutputStream(file);

            final StringWriter writer = new StringWriter();
            IOUtils.copy(getClass().getResourceAsStream("/config-template.conf"), writer, "UTF-8");
            IOUtils.write(writer.toString(), fos, "UTF-8");
            fos.flush();
            fos.close();
            System.out.println("Created config file");
        } else {
            System.err.println("Config file already exists");
        }
    }

    private CombinedListener createListeners(final GalenArguments arguments) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final CombinedListener combinedListener = new CombinedListener();
        combinedListener.add(new ConsoleReportingListener(System.out, System.out));

        // Adding all user defined listeners
        final List<CompleteListener> configuredListeners = getConfiguredListeners();
        for (final CompleteListener configuredListener : configuredListeners) {
            combinedListener.add(configuredListener);
        }
        return combinedListener;
    }

    @SuppressWarnings("unchecked")
    public List<CompleteListener> getConfiguredListeners() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        final List<CompleteListener> configuredListeners = new LinkedList<CompleteListener>();
        final List<String> classNames = GalenConfig.getConfig().getReportingListeners();

        for (final String className : classNames) {
            final Constructor<CompleteListener> constructor = (Constructor<CompleteListener>) Class.forName(className).getConstructor();
            configuredListeners.add(constructor.newInstance());
        }
        return configuredListeners;
    }

    private void performCheck(final GalenArguments arguments, final CombinedListener listener) throws IOException {
        verifyArgumentsForPageCheck(arguments);

        final List<GalenTest> galenTests = new LinkedList<GalenTest>();

        for (final String pageSpecPath : arguments.getPaths()) {
            final GalenBasicTest test = new GalenBasicTest();
            test.setName(pageSpecPath);
            test.setPageTests(asList(new GalenPageTest()
                    .withTitle("Simple check")
                    .withUrl(arguments.getUrl())
                    .withSize(arguments.getScreenSize())
                    .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.FIREFOX))
                    .withActions(
                            asList(new GalenPageActionCheck().withSpecs(asList(pageSpecPath)).withIncludedTags(arguments.getIncludedTags())
                                    .withExcludedTags(arguments.getExcludedTags()).withOriginalCommand(arguments.getOriginal())))));
            galenTests.add(test);
        }

        runTests(new EventHandler(), arguments, galenTests, listener);
    }

    private void verifyArgumentsForPageCheck(final GalenArguments arguments) {
        if (arguments.getUrl() == null) {
            throw new IllegalArgumentException("Url is not specified");
        }

        if (arguments.getScreenSize() == null) {
            throw new IllegalArgumentException("Screen size is not specified");
        }

        if (CollectionUtils.isEmpty(arguments.getPaths())) {
            throw new IllegalArgumentException("There are no specs specified");
        }

    }

    public static void main(final String[] args) throws Exception {
        new GalenMain().execute(GalenArguments.parse(args));
    }

    private void runTests(final GalenArguments arguments, final CompleteListener listener) throws IOException {
        final List<File> basicTestFiles = new LinkedList<File>();
        final List<File> jsTestFiles = new LinkedList<File>();

        for (final String path : arguments.getPaths()) {
            final File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    searchForTests(file, arguments.getRecursive(), basicTestFiles, jsTestFiles);
                } else if (file.isFile()) {
                    final String name = file.getName().toLowerCase();
                    if (name.endsWith(".test")) {
                        basicTestFiles.add(file);
                    } else if (name.endsWith(".test.js")) {
                        jsTestFiles.add(file);
                    }
                }
            } else {
                throw new FileNotFoundException(path);
            }
        }

        if (basicTestFiles.size() > 0 || jsTestFiles.size() > 0) {
            runTestFiles(basicTestFiles, jsTestFiles, listener, arguments);
        } else {
            throw new RuntimeException("Couldn't find any test files");
        }
    }

    private void runTestFiles(final List<File> basicTestFiles, final List<File> jsTestFiles, final CompleteListener listener, final GalenArguments arguments) throws IOException {
        final GalenSuiteReader reader = new GalenSuiteReader();

        final List<GalenTest> tests = new LinkedList<GalenTest>();
        for (final File file : basicTestFiles) {
            tests.addAll(reader.read(file));
        }

        final JsTestCollector testCollector = new JsTestCollector(tests);
        for (final File jsFile : jsTestFiles) {
            testCollector.execute(jsFile);
        }

        testCollector.getEventHandler().invokeBeforeTestSuiteEvents();

        runTests(testCollector.getEventHandler(), arguments, tests, listener);

        testCollector.getEventHandler().invokeAfterTestSuiteEvents();
    }

    private void runTests(final EventHandler eventHandler, final GalenArguments arguments, final List<GalenTest> tests, final CompleteListener listener) {

        if (arguments.getParallelSuites() > 1) {
            runTestsInThreads(eventHandler, tests, arguments, listener, arguments.getParallelSuites());
        } else {
            runTestsInThreads(eventHandler, tests, arguments, listener, 1);
        }
    }

    private void runTestsInThreads(final EventHandler eventHandler,final List<GalenTest> tests,final GalenArguments arguments, final CompleteListener listener,
                                   final int amountOfThreads) {
        final ExecutorService executor = Executors.newFixedThreadPool(amountOfThreads);

        final Pattern filterPattern = createTestFilter(arguments.getFilter());

        final List<GalenTest> filteredTests = filterTests(tests, eventHandler);

        tellBeforeTestSuite(listener, filteredTests);

        final List<GalenTestInfo> testInfos = Collections.synchronizedList(new LinkedList<GalenTestInfo>());

        for (final GalenTest test : filteredTests) {
            if (matchesPattern(test.getName(), filterPattern)) {
                executor.execute(new TestRunnable(test, listener, eventHandler, testInfos));
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        tellAfterTestSuite(listener, testInfos);

        createAllReports(testInfos, arguments);
    }

    private List<GalenTest> filterTests(final List<GalenTest> tests,final  EventHandler eventHandler) {
        final List<TestFilterEvent> filters = eventHandler.getTestFilterEvents();
        if (CollectionUtils.isNotEmpty(filters)) {
            GalenTest[] arrTests = tests.toArray(new GalenTest[]{});

            for (final TestFilterEvent filter : filters) {
                arrTests = filter.execute(arrTests);
            }

            if (arrTests == null) {
                arrTests = new GalenTest[]{};
            }

            return asList(arrTests);
        }
        return tests;
    }

    private void tellBeforeTestSuite(final CompleteListener listener, final List<GalenTest> tests) {
        if (listener != null) {
            try {
                listener.beforeTestSuite(tests);
            } catch (final Exception ex) {
                LOG.error("Unknow error before running testsuites.", ex);
            }
        }
    }

    private void tellAfterTestSuite(final SuiteListener listener, final List<GalenTestInfo> testInfos) {
        if (listener != null) {
            try {
                listener.afterTestSuite(testInfos);
            } catch (final Exception ex) {
                LOG.error("Unknow error after running testsuites.", ex);
            }
        }
    }

    private void createAllReports(final List<GalenTestInfo> testInfos, final GalenArguments arguments) {
        if (arguments.getTestngReport() != null) {
            createTestngReport(arguments.getTestngReport(), testInfos);
        }
        if (arguments.getHtmlReport() != null) {
            createHtmlReport(arguments.getHtmlReport(), testInfos);
        }
        if (arguments.getJsonReport() != null) {
            createJsonReport(arguments.getJsonReport(), testInfos);
        }
    }

    private void createJsonReport(final String jsonReport, final List<GalenTestInfo> testInfos) {
        try {
            new JsonReportBuilder().build(testInfos, jsonReport);
        } catch (final IOException e) {
            LOG.trace("Failed generating json report", e);
        }
    }

    private void createHtmlReport(final String htmlReportPath, final List<GalenTestInfo> testInfos) {
        try {
            new HtmlReportBuilder().build(testInfos, htmlReportPath);
        } catch (final Exception ex) {
            LOG.error("Unknow error during creating HTML report.", ex);
        }
    }

    private void createTestngReport(final String testngReport, final List<GalenTestInfo> testInfos) {
        try {
            new TestNgReportBuilder().build(testInfos, testngReport);
        } catch (final Exception ex) {
            LOG.error("Unknow error during creating TestNG report.", ex);
        }
    }

    private boolean matchesPattern(final String name, final Pattern filterPattern) {
        if (filterPattern != null) {
            return filterPattern.matcher(name).matches();
        } else {
            return true;
        }
    }

    private Pattern createTestFilter(final String filter) {
        return filter != null ? Pattern.compile(filter.replace("*", ".*")) : null;
    }

    private void searchForTests(final File file, final boolean recursive, final List<File> files, final List<File> jsFiles) {

        final String fileName = file.getName().toLowerCase();
        if (file.isFile()) {
            if (fileName.endsWith(".test")) {
                files.add(file);
            } else if (fileName.endsWith(".test.js")) {
                jsFiles.add(file);
            }
        } else if (file.isDirectory()) {
            for (final File childFile : file.listFiles()) {
                searchForTests(childFile, recursive, files, jsFiles);
            }
        }
    }

    public CompleteListener getListener() {
        return listener;
    }

    public void setListener(final CompleteListener listener) {
        this.listener = listener;
    }

}
