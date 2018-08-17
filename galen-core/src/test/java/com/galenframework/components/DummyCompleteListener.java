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
package com.galenframework.components;

import java.util.List;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.runner.CompleteListener;
import com.galenframework.specs.page.PageSection;
import com.galenframework.tests.GalenTest;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.runner.CompleteListener;
import com.galenframework.runner.GalenPageRunner;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.tests.GalenTest;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationResult;

public class DummyCompleteListener implements CompleteListener {

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {

    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
    }

    @Override
    public void onGlobalError(Exception e) {
    }

    @Override
    public void done() {
    }

    @Override
    public void onBeforePageAction(GalenPageAction action) {
    }

    @Override
    public void onAfterPageAction(GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(PageValidation pageValidation, PageSection pageSection) {
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
    public void onTestFinished(GalenTest test) {
    }

    @Override
    public void onTestStarted(GalenTest test) {
    }

    @Override
    public void beforeTestSuite(List<GalenTest> tests) {
    }

    @Override
    public void afterTestSuite(List<GalenTestInfo> tests) {
    }

    
}
