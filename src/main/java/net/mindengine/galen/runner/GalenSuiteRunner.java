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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;


public class GalenSuiteRunner {

    private static final LinkedList<ValidationError> EMPTY_ERRORS = new LinkedList<ValidationError>();
    private SuiteListener suiteListener;
    private ValidationListener validationListener;
    
    public GalenSuiteRunner() {
    }

    public GalenSuiteRunner withSuiteListener(SuiteListener suiteListener) {
        this.setSuiteListener(suiteListener);
        return this;
    }

    public SuiteListener getSuiteListener() {
        return suiteListener;
    }

    public void setSuiteListener(SuiteListener suiteListener) {
        this.suiteListener = suiteListener;
    }

    
    public void runSuite(GalenSuite suite) {
        if (suite == null) {
            throw new IllegalArgumentException("Suite can not be null");
        }
        
        List<GalenPageTest> pageTests = suite.getPageTests();
        
        tellSuiteStarted(suite);
        
        GalenPageRunner pageRunner = new GalenPageRunner();
        pageRunner.setValidationListener(validationListener);
        
        for (GalenPageTest pageTest : pageTests) {
            Browser browser = pageTest.getBrowserFactory().openBrowser();
            
            tellBeforePage(pageRunner, pageTest, browser);
            List<ValidationError> errors = runPageTest(pageRunner, pageTest, browser);
            tellAfterPage(pageRunner, pageTest, browser, errors);
            
            browser.quit();
        }
        
        tellSuiteFinished(suite);
    }

    private void tellAfterPage(GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser, List<ValidationError> errors) {
        try {
            if (suiteListener != null) {
                suiteListener.onAfterPage(this, pageRunner, pageTest, browser, errors);
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

    private List<ValidationError> runPageTest(GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        try {
            return pageRunner.run(browser, pageTest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_ERRORS;
    }

    private void tellSuiteFinished(GalenSuite suite) {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteFinished(this, suite);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellSuiteStarted(GalenSuite suite) {
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
