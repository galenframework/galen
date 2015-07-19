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
package com.galenframework.runner;

import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationListener;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationListener;
import com.galenframework.validation.ValidationResult;

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
            validationListener.onAfterPageAction(action);
        }
    }

    private void tellBeforeAction(GalenPageAction action) {
        if (validationListener != null) {
            validationListener.onBeforePageAction(action);
        } 
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onObject(pageValidation, objectName);
        }
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
        if (validationListener != null) {
            validationListener.onAfterObject(pageValidation, objectName);
        }
    }

    @Override
    public void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {
        if (validationListener != null) {
            validationListener.onBeforeSpec(pageValidation, objectName, spec);
        }
    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        if (validationListener != null) {
            validationListener.onSpecError(pageValidation, objectName, spec, result);
        }
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        if (validationListener != null) {
            validationListener.onSpecSuccess(pageValidation, objectName, spec, result);
        }
    }

    @Override
    public void onGlobalError(Exception e) {
        if (validationListener != null) {
            validationListener.onGlobalError(e);
        }
    }

    @Override
    public void onBeforePageAction(GalenPageAction action) {
        if (validationListener != null) {
            this.onBeforePageAction(action);
        }
    }

    @Override
    public void onAfterPageAction(GalenPageAction action) {
        if (validationListener != null) {
            this.onAfterPageAction(action);
        }
    }

    @Override
    public void onBeforeSection(PageValidation pageValidation, PageSection pageSection) {
        if (validationListener != null) {
            validationListener.onBeforeSection(pageValidation, pageSection);
        }
    }

    @Override
    public void onAfterSection(PageValidation pageValidation, PageSection pageSection) {
        if (validationListener != null) {
            validationListener.onAfterSection(pageValidation, pageSection);
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

    @Override
    public void onSpecGroup(PageValidation pageValidation, String specGroupName) {
        if (validationListener != null) {
            validationListener.onSpecGroup(pageValidation, specGroupName);
        }
    }

    @Override
    public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {
        if (validationListener != null) {
            validationListener.onAfterSpecGroup(pageValidation, specGroupName);
        }
    }

    public TestReport getReport() {
        return report;
    }

    public void setReport(TestReport report) {
        this.report = report;
    }

}
