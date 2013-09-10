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
package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;


public class GalenPageRunner {

    private ValidationListener validationListener;
    
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

    public List<ValidationError> run(Browser browser, GalenPageTest pageTest) {
        if (pageTest.getScreenSize() != null) {
            browser.changeWindowSize(pageTest.getScreenSize());
        }
        
        browser.load(pageTest.getUrl());
        
        List<ValidationError> allErrors = new LinkedList<ValidationError>();
        
        for (GalenPageAction action : pageTest.getActions()) {
            try {
                List<ValidationError> errors = action.execute(browser, pageTest, validationListener);
                if (errors != null) {
                    allErrors.addAll(errors);
                }
            }
            catch (Exception e) {
                validationListener.onGlobalError(e);
                allErrors.add(ValidationError.fromException(e));
            }
        }
        
        return allErrors;
    }

}
