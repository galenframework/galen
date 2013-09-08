package net.mindengine.galen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.reports.ConsoleReportingListener;
import net.mindengine.galen.reports.HtmlReportingListener;
import net.mindengine.galen.reports.TestngReportingListener;
import net.mindengine.galen.runner.CombinedListener;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenArguments;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.suite.reader.GalenSuiteReader;

public class GalenMain {
    
    List<CompleteListener> listeners;

    public void execute(GalenArguments arguments) throws IOException {
        CombinedListener combinedListener = new CombinedListener();
        
        combinedListener.add(new ConsoleReportingListener(System.out, System.err));
        
        if (arguments.getHtmlReport() != null) {
            combinedListener.add(new HtmlReportingListener(arguments.getHtmlReport()));
        }
        if (arguments.getTestngReport() != null) {
            combinedListener.add(new TestngReportingListener(arguments.getTestngReport()));
        }
        
        if ("test".equals(arguments.getAction())) {
            runTests(arguments, combinedListener);
        }
        
        combinedListener.done();
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
            runTestFiles(testFiles, listener);
        }
        else {
            throw new RuntimeException("Couldn't find any test files");
        }
    }

    private void runTestFiles(List<File> testFiles, CompleteListener listener) throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> suites = new LinkedList<GalenSuite>();
        for (File file : testFiles) {
            suites.addAll(reader.read(file));
        }
        
        runSuites(suites, listener);
    }

    private void runSuites(List<GalenSuite> suites, CompleteListener listener) {
        GalenSuiteRunner suiteRunner = new GalenSuiteRunner();
        suiteRunner.setSuiteListener(listener);
        suiteRunner.setValidationListener(listener);
        
        for (GalenSuite suite : suites) {
            suiteRunner.runSuite(suite);
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
