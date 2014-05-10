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
package net.mindengine.galen.runner;

import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.tests.GalenBasicTest;
import net.mindengine.galen.validation.ValidationListener;


public class GalenBasicTestRunner {

    private SuiteListener suiteListener;
    private ValidationListener validationListener;
    
    public GalenBasicTestRunner() {
    }

    public GalenBasicTestRunner withSuiteListener(SuiteListener suiteListener) {
        this.setSuiteListener(suiteListener);
        return this;
    }

    public SuiteListener getSuiteListener() {
        return suiteListener;
    }

    public void setSuiteListener(SuiteListener suiteListener) {
        this.suiteListener = suiteListener;
    }

    
    public TestReport runTest(GalenBasicTest test) throws Exception {
        if (test == null) {
            throw new IllegalArgumentException("Test can not be null");
        }
        
        List<GalenPageTest> pageTests = test.getPageTests();
        
        tellSuiteStarted(test);
        
        TestReport report = new TestReport();
        
        GalenPageRunner pageRunner = new GalenPageRunner(report);
        pageRunner.setValidationListener(validationListener);
        
        for (GalenPageTest pageTest : pageTests) {
            report.gotoRoot();
            report.sectionStart(pageTest.getTitle());
            
            Browser browser = pageTest.getBrowserFactory().openBrowser();
        
            tellBeforePage(pageRunner, pageTest, browser);
            pageRunner.run(browser, pageTest);
            tellAfterPage(pageRunner, pageTest, browser);
            
            browser.quit();
            report.sectionEnd();
        }
        
        tellSuiteFinished(test);
        
        return report;
    }

    private void tellAfterPage(GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        try {
            if (suiteListener != null) {
                suiteListener.onAfterPage(this, pageRunner, pageTest, browser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellBeforePage(GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        try {
            if (suiteListener != null) {
                suiteListener.onBeforePage(this, pageRunner, pageTest, browser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void tellSuiteFinished(GalenBasicTest suite) {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteFinished(this, suite);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellSuiteStarted(GalenBasicTest suite) {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteStarted(this, suite);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }


}
