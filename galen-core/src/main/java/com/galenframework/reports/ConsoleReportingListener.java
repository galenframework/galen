/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.config.GalenConfig;
import com.galenframework.runner.CompleteListener;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.tests.GalenTest;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationResult;

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
    
    private ThreadLocal<Integer> currentObjectLevel = new ThreadLocal<>();
    
    private int logLevel = getLogLevel();
    
    public ConsoleReportingListener(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    private int getLogLevel() {
        return GalenConfig.getConfig().getLogLevel();
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        if (logLevel >= OBJECT_LEVEL) {
            increaseCurrentObjectLevel();
            
            out.print(getObjectIndentation());
            out.print(objectName);
            out.println(":");
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
    public void onAfterObject(PageValidation pageValidation, String objectName) {
        decreaseCurrentObjectLevel();
        if (logLevel >= OBJECT_LEVEL) {
            out.println();
        }
    }

    @Override
    public void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {

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
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        if (logLevel >= OBJECT_SPEC_LEVEL) {
            err.print(getSpecErrorIndentation());
            err.println(spec.toText());
            if (result.getError().getMessages() != null) {
                for (String message : result.getError().getMessages()) {
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
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
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
    public void onGlobalError(Exception e) {
        e.printStackTrace(err);
    }

    @Override
    public void onBeforePageAction(GalenPageAction action) {
        if (logLevel > PAGE_LEVEL) {
            out.println(action.getOriginalCommand());
        }
    }
    
    @Override
    public void onAfterPageAction(GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(PageValidation pageValidation, PageSection pageSection) {
        if (logLevel >= SECTION_LEVEL) {
            out.print("= ");
            String name = pageSection.getName();
            if (name != null && !name.trim().isEmpty()) {
                out.print(pageSection.getName());
            }
            else {
                out.print("Unnamed");
            }
            out.println(" =");
        }
    }

    @Override
    public void onAfterSection(PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void onSubLayout(PageValidation pageValidation, String objectName) {

    }

    @Override
    public void onAfterSubLayout(PageValidation pageValidation, String objectName) {

    }

    @Override
    public void onSpecGroup(PageValidation pageValidation, String specGroupName) {

    }

    @Override
    public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {

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
        
        List<String> failedTests = new LinkedList<>();
        
        TestStatistic allStatistic = new TestStatistic();
        
        for (GalenTestInfo test : tests) {
            TestStatistic statistic = test.getReport().fetchStatistic();
            allStatistic.add(statistic);
            if (test.getException() != null || statistic.getErrors() > 0) {
                failedTests.add(test.getName());
            }
        }
        
        if (failedTests.size() > 0) {
            out.println("Failed tests:");
            for (String name: failedTests) {
                out.println("    " + name);
            }
            out.println();
        }
        
        out.print("Suite status: ");
        if (failedTests.size() > 0) {
            out.println("FAIL");
        }
        else {
            out.println("PASS");
        }

        out.println("Total tests: " + tests.size());
        out.println("Total failed tests: " + failedTests.size());
        out.println("Total failures: " + allStatistic.getErrors());
    }
}
