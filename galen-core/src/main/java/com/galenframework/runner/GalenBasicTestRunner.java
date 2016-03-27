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
package com.galenframework.runner;

import java.util.List;

import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.tests.GalenBasicTest;
import com.galenframework.validation.ValidationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.tests.GalenBasicTest;
import com.galenframework.validation.ValidationListener;


public class GalenBasicTestRunner {

    private final static Logger LOG = LoggerFactory.getLogger(GalenBasicTestRunner.class);

    private TestListener testListener;
    private ValidationListener validationListener;
    
    public GalenBasicTestRunner() {
    }

    public GalenBasicTestRunner withSuiteListener(TestListener suiteListener) {
        this.setSuiteListener(suiteListener);
        return this;
    }

    public TestListener getSuiteListener() {
        return testListener;
    }

    public void setSuiteListener(TestListener suiteListener) {
        this.testListener = suiteListener;
    }

    
    public TestReport runTest(TestReport report, GalenBasicTest test) throws Exception {
        if (test == null) {
            throw new IllegalArgumentException("Test can not be null");
        }
        
        List<GalenPageTest> pageTests = test.getPageTests();
        
        GalenPageRunner pageRunner = new GalenPageRunner(report);
        pageRunner.setValidationListener(validationListener);
        
        for (GalenPageTest pageTest : pageTests) {
            report.gotoRoot();
            report.sectionStart(pageTest.getTitle());
            
            Browser browser = pageTest.getBrowserFactory().openBrowser();

            try {
                pageRunner.run(browser, pageTest);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                report.error(ex);
            }

            try {
                browser.quit();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            report.sectionEnd();
        }
        
        return report;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }


}
