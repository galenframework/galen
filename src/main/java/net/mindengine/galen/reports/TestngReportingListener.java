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
package net.mindengine.galen.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestngReportingListener implements CompleteListener {

    private String reportPath;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private String formatDate(Date date) {
        return sdf.format(date);
    }
    
    public class Error {
        private String stackTrace;
        private String path;
        public Error(String path, String stackTrace) {
            this.path = path;
            this.stackTrace = stackTrace;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getStackTrace() {
            return stackTrace;
        }
        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }
    }
    
    public class TestMethod {
        private String name = "";
        private Date startedAt;
        private Date endedAt;
        private String status = "PASS";
        private Error error;
        
        public String getStartedAtFormatted() {
            return formatDate(startedAt);
        }
        public String getEndedAtFormatted() {
            return formatDate(endedAt);
        }
        public Date getStartedAt() {
            return startedAt;
        }
        public void setStartedAt(Date startedAt) {
            this.startedAt = startedAt;
        }
        public Date getEndedAt() {
            return endedAt;
        }
        public void setEndedAt(Date endedAt) {
            this.endedAt = endedAt;
        }
        public Long getDuration() {
            if (endedAt != null && startedAt != null) {
                return endedAt.getTime() - startedAt.getTime();
            }
            else return 0L;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void markAsFailed() {
            setStatus("FAIL");
        }
        public Error getError() {
            return error;
        }
        public void setError(Error error) {
            this.error = error;
        }
    }
    
    public class TestClass {
        private String name;
        private List<TestMethod> testMethods = new LinkedList<TestMethod>();
        private TestClass(String name) {
            this.setName(name);
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<TestMethod> getTestMethods() {
            return testMethods;
        }
        public void setTestMethods(List<TestMethod> testMethods) {
            this.testMethods = testMethods;
        }
    }
    
    public class TestRun {
        private String name;
        private List<TestClass> testClasses = new LinkedList<TestClass>();
        private TestRun(String name) {
            this.setName(name);
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<TestClass> getTestClasses() {
            return testClasses;
        }
        public void setTestClasses(List<TestClass> testClasses) {
            this.testClasses = testClasses;
        }
    }
    
    private List<TestRun> testRuns = new LinkedList<TestRun>();
    
    private ThreadLocal<TestRun> currentTestRun = new ThreadLocal<TestRun>();
    private ThreadLocal<TestClass> currentTestClass = new ThreadLocal<TestClass>();
    private ThreadLocal<TestMethod> currentTestMethod = new ThreadLocal<TestMethod>();
    
    public TestngReportingListener(String reportPath) {
        this.reportPath = reportPath;
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser,
            List<ValidationError> errors) {
        currentTestClass.remove();
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        String title = pageTest.getTitle();
        if (title == null) {
            title = pageTest.getUrl() + " " + GalenUtils.formatScreenSize(pageTest.getScreenSize());
        }
        TestClass testClass = new TestClass(title);
        currentTestClass.set(testClass);
        currentTestRun.get().testClasses.add(testClass);
    }


    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        currentTestRun.remove();
    }
    

    @Override
    public synchronized void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        TestRun testRun = new TestRun(suite.getName());
        currentTestRun.set(testRun);
        testRuns.add(testRun);
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        currentTestMethod.get().markAsFailed();
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
    }

    
    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }
    
    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        TestMethod testMethod = new TestMethod();
        testMethod.setStartedAt(new Date());
        testMethod.setName(action.getOriginalCommand());
        
        currentTestMethod.set(testMethod);
        currentTestClass.get().testMethods.add(testMethod);
    }
    
    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        currentTestMethod.get().setEndedAt(new Date());
    }

    @Override
    public void done() {
        try {
            Template template  = new Template("testng-report", new InputStreamReader(getClass().getResourceAsStream("/testng-report/testng-report.ftl.xml")), new Configuration());
            
            File file = new File(this.reportPath);
            
            makeSurePathExists(file);
            
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Cannot create file: " + file.getAbsolutePath());
                }
            }
            
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("testRuns", testRuns);
            
            FileWriter fileWriter = new FileWriter(file);
            template.process(model, fileWriter);
            fileWriter.flush();
            fileWriter.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeSurePathExists(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not create path: " + parentDir.getAbsolutePath()); 
            }
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        TestClass testClass = currentTestClass.get();
        if (testClass != null) {

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            
            TestMethod method = new TestMethod();
            method.setName("global error");
            method.setStartedAt(new Date());
            method.markAsFailed();
            method.setEndedAt(new Date());
            method.setError(new Error(e.getClass().getName(), sw.getBuffer().toString()));
            
            testClass.testMethods.add(method);
        }
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
    }
}
