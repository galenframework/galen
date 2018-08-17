/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation;

import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;

public interface ValidationListener {
    
    void onObject(PageValidation pageValidation, String objectName);
    
    void onAfterObject(PageValidation pageValidation, String objectName);

    void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec);

    void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult validationResult);

    void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult validationResult);

    void onGlobalError(Exception e);

    void onBeforePageAction(GalenPageAction action);

    void onAfterPageAction(GalenPageAction action);
    
    void onBeforeSection(PageValidation pageValidation, PageSection pageSection);
    
    void onAfterSection(PageValidation pageValidation, PageSection pageSection);

    void onSubLayout(PageValidation pageValidation, String objectName);

    void onAfterSubLayout(PageValidation pageValidation, String objectName);

    void onSpecGroup(PageValidation pageValidation, String specGroupName);

    void onAfterSpecGroup(PageValidation pageValidation, String specGroupName);

}
