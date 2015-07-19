/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.components.validation;

import com.galenframework.specs.page.PageSection;
import com.galenframework.validation.ValidationListener;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationListener;
import com.galenframework.validation.ValidationResult;

public class TestValidationListener implements ValidationListener {

    private StringBuffer invokations = new StringBuffer();

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        append("<" + spec.getClass().getSimpleName() + " " + objectName + ">");
        StringBuffer buffer = new StringBuffer();
        for (String message : result.getError().getMessages()) {
            buffer.append("<msg>");
            buffer.append(message);
            buffer.append("</msg>");
        }
        append("<e>" + buffer.toString() + "</e>");
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        append("<" + spec.getClass().getSimpleName() + " " + objectName + ">");
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        append("<o " + objectName + ">");
    }

    private void append(String text) {
        invokations.append(text);
        invokations.append('\n');
    }
    
    public String getInvokations() {
        return invokations.toString();
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
        append("</o " + objectName + ">");
    }

    @Override
    public void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {

    }

    @Override
    public void onGlobalError(Exception e) {
        invokations.append("<global-error " + e.getClass().getSimpleName() + ">" + e.getMessage() + "</global-error>");
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

}
