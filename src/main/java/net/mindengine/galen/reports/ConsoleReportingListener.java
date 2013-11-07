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
package net.mindengine.galen.reports;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class ConsoleReportingListener implements CompleteListener {

    private static final String SPEC_ERROR_MESSAGE_INDENTATION_SUFFIX   = ":   ";
    private static final String SPEC_ERROR_INDENTATION_HEADER           = "->  ";
    private static final String NORMAL_INDETATION                       = "    ";
    private PrintStream out;
    private PrintStream err;
    
    private int totalCount = 0;
    private int errorCount = 0;
    private Set<String> suitesWithError = new HashSet<String>();
    private String currentSuite;

    private ThreadLocal<Integer> currentObjectLevel = new ThreadLocal<Integer>();
    
    public ConsoleReportingListener(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        increaseCurrentObjectLevel();
        
        out.print(getObjectIndentation());
        out.println(objectName);
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
        out.println();
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
        errorCount++;
        totalCount++;
        suitesWithError.add(currentSuite);
        
        err.print(getSpecErrorIndentation());
        err.println(spec.toText());
        for(String message : error.getMessages()) {
            err.print(getSpecErrorIndentation());
            err.print(SPEC_ERROR_MESSAGE_INDENTATION_SUFFIX);
            err.println(message);
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
        totalCount++;
        out.print(getObjectIndentation());
        out.print(NORMAL_INDETATION);
        out.println(spec.toText());
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser,
            List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        out.println("----------------------------------------");
        out.print("Page: ");
        
        if (pageTest.getTitle() != null) {
            out.println(pageTest.getTitle());
        }
        else {
            out.print(pageTest.getUrl());
            if (pageTest.getScreenSize() != null) {
                out.print(" ");
                out.println(GalenUtils.formatScreenSize(pageTest.getScreenSize()));
            }
            else {
                out.println();
            }
        }
        
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        currentSuite = suite.getName();
        
        out.println("========================================");
        out.print("Suite: ");
        out.println(suite.getName());
        out.println("========================================");
    }

    
    @Override
    public void done() {
        out.println();
        out.println("========================================");
        out.println("----------------------------------------");
        out.println("========================================");
        if (suitesWithError.size() > 0) {
            out.println("Failed suites:");
            for (String name: suitesWithError) {
                out.println("    " + name);
            }
            out.println();
        }
        
        out.print("Status: ");
        if (errorCount > 0) {
            out.println("FAIL");
            out.println("Total failures: " + errorCount);
        }
        else {
            out.println("PASS");
        }
        out.println("Total tests: " + totalCount);
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        errorCount++;
        e.printStackTrace(err);
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        out.println(action.getOriginalCommand());
    }
    
    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        out.print("@ ");
        out.println(pageSection.getName());
        out.println("-------------------------");
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
    }
}
