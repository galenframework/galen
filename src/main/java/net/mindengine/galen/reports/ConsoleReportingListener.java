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
package net.mindengine.galen.reports;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

public class ConsoleReportingListener implements CompleteListener {

    private static final String SPEC_ERROR_MESSAGE_INDENTATION_SUFFIX   = ":   ";
    private static final String SPEC_ERROR_INDENTATION_HEADER           = "->  ";
    private static final String NORMAL_INDETATION                       = "    ";
    private static final int TEST_LEVEL = 1;
    private static final int PAGE_LEVEL = 2;
    private static final int SECTION_LEVEL = 3;
    private static final int OBJECT_LEVEL = 4;
    private static final int OBJECT_SPEC_LEVEL = 5;
    
    private PrintStream out;
    private PrintStream err;
    
    private ThreadLocal<Integer> currentObjectLevel = new ThreadLocal<Integer>();
    
    private int logLevel = getLogLevel();
    
    public ConsoleReportingListener(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    private int getLogLevel() {
        return GalenConfig.getConfig().getLogLevel();
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (logLevel >= OBJECT_LEVEL) {
            increaseCurrentObjectLevel();
            
            out.print(getObjectIndentation());
            out.println(objectName);
        }
    }
    
    private String getObjectIndentation() {
        Integer level = currentObjectLevel.get();
        if (level != null && level > 0) {
            StringBuffer buffer = new StringBuffer(NORMAL_INDETATION);
            for (int i =0; i <= level; i++) {
                buffer.append(NORMAL_INDETATION);
            }
            return buffer.toString();
        }
        return NORMAL_INDETATION;
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        decreaseCurrentObjectLevel();
        if (logLevel >= OBJECT_LEVEL) {
            out.println();
        }
    }

    private void decreaseCurrentObjectLevel() {
        Integer value = currentObjectLevel.get();
        if (value != null) {
            if (value > 0) {
                value = value - 1;
                currentObjectLevel.set(value);
            }
            else {
                currentObjectLevel.remove();
            }
        }
        
    }

    private void increaseCurrentObjectLevel() {
        Integer value = currentObjectLevel.get();
        if (value == null) {
            currentObjectLevel.set(0);
        }
        else {
            value = value + 1;
            currentObjectLevel.set(value);
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        if (logLevel >= OBJECT_SPEC_LEVEL) {
            err.print(getSpecErrorIndentation());
            err.println(spec.toText());
            if (error.getMessages() != null) {
                for (String message : error.getMessages()) {
                    err.print(getSpecErrorIndentation());
                    err.print(SPEC_ERROR_MESSAGE_INDENTATION_SUFFIX);
                    err.println(message);
                }
            }
        }
    }

    private String getSpecErrorIndentation() {
        Integer level = currentObjectLevel.get();
        if (level != null && level > 0) {
            StringBuffer buffer = new StringBuffer(SPEC_ERROR_INDENTATION_HEADER);
            for (int i =0; i <= level + 1; i++) {
                buffer.append(NORMAL_INDETATION);
            }
            return buffer.toString();
        }
        return SPEC_ERROR_INDENTATION_HEADER + NORMAL_INDETATION;
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        if (logLevel >= OBJECT_SPEC_LEVEL) {
            out.print(getObjectIndentation());
            out.print(NORMAL_INDETATION);
            out.println(spec.toText());
        }
    }

    
    @Override
    public void onTestFinished(GalenTest test) {
    }

    @Override
    public void onTestStarted(GalenTest test) {
        if (logLevel >= TEST_LEVEL) {
            out.println("========================================");
            out.print("Test: ");
            out.println(test.getName());
            out.println("========================================");
        }
    }

    
    @Override
    public void done() {
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        e.printStackTrace(err);
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        if (logLevel > PAGE_LEVEL) {
            out.println(action.getOriginalCommand());
        }
    }
    
    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        if (logLevel >= SECTION_LEVEL) {
            out.print("@ ");
            String name = pageSection.getName();
            if (name != null && !name.trim().isEmpty()) {
                out.println(pageSection.getName());
            }
            else {
                out.println("Unnamed");
            }
            out.println("-------------------------");
        }
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void beforeTestSuite(List<GalenTest> tests) {
    }

    @Override
    public void afterTestSuite(List<GalenTestInfo> tests) {
        out.println();
        out.println("========================================");
        out.println("----------------------------------------");
        out.println("========================================");
        
        List<String> failedTests = new LinkedList<String>();
        
        TestStatistic allStatistic = new TestStatistic();
        
        for (GalenTestInfo test : tests) {
            TestStatistic statistic = test.getReport().fetchStatistic();
            allStatistic.add(statistic);
            if (test.getException() != null || statistic.getErrors() > 0) {
                failedTests.add(test.getName());
            }
        }
        
        if (failedTests.size() > 0) {
            out.println("Failed suites:");
            for (String name: failedTests) {
                out.println("    " + name);
            }
            out.println();
        }
        
        out.print("Status: ");
        if (failedTests.size() > 0) {
            out.println("FAIL");
            out.println("Total failures: " + allStatistic.getErrors());
        }
        else {
            out.println("PASS");
        }
    }
}
