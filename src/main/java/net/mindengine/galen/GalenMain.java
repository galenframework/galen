/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.reports.ConsoleReportingListener;
import net.mindengine.galen.reports.HtmlReportingListener;
import net.mindengine.galen.reports.TestngReportingListener;
import net.mindengine.galen.runner.CombinedListener;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenArguments;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.reader.GalenSuiteReader;

import org.apache.commons.cli.ParseException;

public class GalenMain {
    
    List<CompleteListener> listeners;

    public void execute(GalenArguments arguments) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        CombinedListener combinedListener = createListeners(arguments);
        
        if ("test".equals(arguments.getAction())) {
            runTests(arguments, combinedListener);
        }
        else if ("check".equals(arguments.getAction())) {
            performCheck(arguments, combinedListener);
        }
        
        combinedListener.done();
    }

    private CombinedListener createListeners(GalenArguments arguments) throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        CombinedListener combinedListener = new CombinedListener();
        combinedListener.add(new ConsoleReportingListener(System.out, System.err));
        
        if (arguments.getHtmlReport() != null) {
            combinedListener.add(new HtmlReportingListener(arguments.getHtmlReport()));
        }
        if (arguments.getTestngReport() != null) {
            combinedListener.add(new TestngReportingListener(arguments.getTestngReport()));
        }
        
        //Adding all user defined listeners
        List<CompleteListener> configuredListeners = getConfiguredListeners();
        for (CompleteListener configuredListener : configuredListeners) {
            combinedListener.add(configuredListener);
        }
        
        return combinedListener;
    }
    
    @SuppressWarnings("unchecked")
    public List<CompleteListener> getConfiguredListeners() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<CompleteListener> configuredListeners = new LinkedList<CompleteListener>();
        List<String> classNames = GalenConfig.getConfig().getReportingListeners();
        
        for (String className : classNames) {
            Constructor<CompleteListener> constructor = (Constructor<CompleteListener>) Class.forName(className).getConstructor();
            configuredListeners.add(constructor.newInstance());
        }
        return configuredListeners;
    }

    private void performCheck(GalenArguments arguments, CombinedListener listener) throws IOException {
        verifyArgumentsForPageCheck(arguments);
        
        List<GalenSuite> galenSuites = new LinkedList<GalenSuite>();
        
        
        for (String pageSpecPath : arguments.getPaths()) {
            GalenSuite suite = new GalenSuite();
            
            suite.setName(pageSpecPath);
            
            
            suite.setPageTests(asList(new GalenPageTest()
                .withUrl(arguments.getUrl())
                .withSize(arguments.getScreenSize())
                .withBrowserFactory(new SeleniumBrowserFactory(SeleniumBrowserFactory.FIREFOX))
                .withActions(asList((GalenPageAction)new GalenPageActionCheck()
                    .withSpecs(asList(pageSpecPath))
                    .withIncludedTags(arguments.getIncludedTags())
                    .withExcludedTags(arguments.getExcludedTags()))
                )));
                        
            galenSuites.add(suite);
        }
        
        runSuites(arguments, galenSuites, listener);
    }

    private void verifyArgumentsForPageCheck(GalenArguments arguments) {
        if (arguments.getUrl() == null) {
            throw new IllegalArgumentException("Url is not specified");
        }
        
        if (arguments.getScreenSize() == null) {
            throw new IllegalArgumentException("Screen size is not specified");
        }
        
        if (arguments.getPaths().size() < 1) {
            throw new IllegalArgumentException("There are no specs specified");
        }
        
    }

    public static void main(String[] args) throws ParseException, IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        new GalenMain().execute(GalenArguments.parse(args));
    }

    private void runTests(GalenArguments arguments, CompleteListener listener) throws IOException {
        List<File> testFiles = new LinkedList<File>();
        
        for (String path : arguments.getPaths()) {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    searchForTests(file, arguments.getRecursive(), testFiles);
                }
                else if (file.isFile()) {
                    testFiles.add(file);
                }
            }
            else {
                throw new FileNotFoundException(path);
            }
        }
        
        if (testFiles.size() > 0) {
            runTestFiles(testFiles, listener, arguments);
        }
        else {
            throw new RuntimeException("Couldn't find any test files");
        }
    }

    private void runTestFiles(List<File> testFiles, CompleteListener listener, GalenArguments arguments) throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> suites = new LinkedList<GalenSuite>();
        for (File file : testFiles) {
            suites.addAll(reader.read(file));
        }
        
        runSuites(arguments, suites, listener);
    }

    private void runSuites(GalenArguments arguments, List<GalenSuite> suites, CompleteListener listener) {
        
        if (arguments.getParallelSuites() > 1) {
            runSuitesInThreads(suites, arguments.getParallelSuites(), listener);
        }
        else {
            runSuitesInSingleThread(suites, listener);
        }
    }

    private void runSuitesInSingleThread(List<GalenSuite> suites, CompleteListener listener) {
        GalenSuiteRunner suiteRunner = new GalenSuiteRunner();
        suiteRunner.setSuiteListener(listener);
        suiteRunner.setValidationListener(listener);
        
        for (GalenSuite suite : suites) {
            suiteRunner.runSuite(suite);
        }
    }

    private void runSuitesInThreads(List<GalenSuite> suites, int parallelSuites, final CompleteListener listener) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelSuites);
        for (final GalenSuite suite : suites) {
            Runnable thread = new Runnable() {
                @Override
                public void run() {
                    GalenSuiteRunner suiteRunner = new GalenSuiteRunner();
                    suiteRunner.setSuiteListener(listener);
                    suiteRunner.setValidationListener(listener);
                    suiteRunner.runSuite(suite);
                }
            };
            
            executor.execute(thread);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private void searchForTests(File file, boolean recursive, List<File> files) {
        if (file.isFile() && file.getName().toLowerCase().endsWith(".test")) {
            files.add(file);
        }
        else if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                searchForTests(childFile, recursive, files);
            }
        }
    }

}
