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
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

public class ConsoleReportingListener implements CompleteListener {

    private static final String OBJECT_INDETATION = "    ";
    private static final String SPEC_ERROR_INDENTATION = "->      ";
    private static final String SPEC_ERROR_MESSAGE_INDENTATION = "->      :     ";
    private static final String SPEC_INDENTATION = "        ";
    private PrintStream out;
    private PrintStream err;
    
    private int totalCount = 0;
    private int errorCount = 0;
    private Set<String> suitesWithError = new HashSet<String>();
    private String currentSuite;

    public ConsoleReportingListener(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        out.print(OBJECT_INDETATION);
        out.println(objectName);
    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        errorCount++;
        totalCount++;
        suitesWithError.add(currentSuite);
        
        err.print(SPEC_ERROR_INDENTATION);
        err.println(spec.toText());
        for(String message : error.getMessages()) {
            err.print(SPEC_ERROR_MESSAGE_INDENTATION);
            err.println(message);
        }
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        totalCount++;
        out.print(SPEC_INDENTATION);
        out.println(spec.toText());
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser,
            List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser) {
        out.println("----------------------------------------");
        out.print("Page: ");
        out.print(pageTest.getUrl());
        out.print(" ");
        out.println(GalenUtils.formatScreenSize(pageTest.getScreenSize()));
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
    public void onAfterObject(PageValidation pageValidation, String objectName) {
        out.println();
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
    public void onGlobalError(Exception e) {
        errorCount++;
        e.printStackTrace(err);
    }

}
