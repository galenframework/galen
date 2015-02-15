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
package net.mindengine.galen.runner;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

/**
 * Implements ValidationListener as a proxy listener so it can pass itself to it. 
 * @author ishubin
 *
 */
public class GalenPageRunner implements ValidationListener {

    private ValidationListener validationListener;
    private TestReport report;
    
    public GalenPageRunner(TestReport report) {
        this.setReport(report);
    }

    public GalenPageRunner withValidationListener(ValidationListener validationListener) {
        this.setValidationListener(validationListener);
        return this;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public void run(Browser browser, GalenPageTest pageTest) throws Exception {
        
        if (pageTest.getScreenSize() != null) {
            browser.changeWindowSize(pageTest.getScreenSize());
        }
        
        if (pageTest.getUrl() != null && !pageTest.getUrl().isEmpty()) {
            browser.load(pageTest.getUrl());
        }
        
        for (GalenPageAction action : pageTest.getActions()) {
            tellBeforeAction(action);
            
            report.sectionStart(action.getOriginalCommand());
            executeAction(browser, pageTest, action);
            
            report.sectionEnd();
            tellAfterAction(action);
        }
    }

    private void executeAction(Browser browser, GalenPageTest pageTest, GalenPageAction action) throws Exception {
        action.execute(report, browser, pageTest, this);
    }

    
   
    private void tellAfterAction(GalenPageAction action) {
        if (validationListener != null) {
            validationListener.onAfterPageAction(this, action);
        }
    }

    private void tellBeforeAction(GalenPageAction action) {
        if (validationListener != null) {
            validationListener.onBeforePageAction(this, action);
        } 
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onObject(this, pageValidation, objectName);     
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onAfterObject(this, pageValidation, objectName);
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        if (validationListener != null) {
            validationListener.onSpecError(this, pageValidation, objectName, spec, error);
        }
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        if (validationListener != null) {
            validationListener.onSpecSuccess(this, pageValidation, objectName, spec);
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        if (validationListener != null) {
            validationListener.onGlobalError(this, e);
        }
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        if (validationListener != null) {
            this.onBeforePageAction(this, action);
        }
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        if (validationListener != null) {
            this.onAfterPageAction(this, action);
        }
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        if (validationListener != null) {
            validationListener.onBeforeSection(this, pageValidation, pageSection);
        }
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        if (validationListener != null) {
            validationListener.onAfterSection(this, pageValidation, pageSection);
        }
    }

    @Override
    public void onSubLayout(PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onSubLayout(pageValidation, objectName);
        }
    }

    @Override
    public void onAfterSubLayout(PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onAfterSubLayout(pageValidation, objectName);
        }
    }

    public TestReport getReport() {
        return report;
    }

    public void setReport(TestReport report) {
        this.report = report;
    }

}
