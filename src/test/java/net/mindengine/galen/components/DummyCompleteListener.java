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
package net.mindengine.galen.components;

import java.util.List;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationResult;

public class DummyCompleteListener implements CompleteListener {

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
    }

    @Override
    public void done() {
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
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
