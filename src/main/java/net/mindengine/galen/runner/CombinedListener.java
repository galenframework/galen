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
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

public class CombinedListener implements CompleteListener {

    List<CompleteListener> listeners = new LinkedList<CompleteListener>();
    
    public void add(CompleteListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser,
            List<ValidationError> errors) {
        for (CompleteListener listener : listeners) {
            listener.onAfterPage(galenSuiteRunner, pageRunner, pageTest, browser, errors);
        }
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        for (CompleteListener listener : listeners) {
            listener.onBeforePage(galenSuiteRunner, pageRunner, pageTest, browser);
        }
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        for (CompleteListener listener : listeners) {
            listener.onSuiteFinished(galenSuiteRunner, suite);
        }
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        for (CompleteListener listener : listeners) {
            listener.onSuiteStarted(galenSuiteRunner, suite);
        }
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (CompleteListener listener : listeners) {
            listener.onObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (CompleteListener listener : listeners) {
            listener.onAfterObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        for (CompleteListener listener : listeners) {
            listener.onSpecError(pageRunner, pageValidation, objectName, spec, error);
        }
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        for (CompleteListener listener : listeners) {
            listener.onSpecSuccess(pageRunner, pageValidation, objectName, spec);
        }
    }

    @Override
    public void done() {
        for (CompleteListener listener : listeners) {
            listener.done();
        } 
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        for (CompleteListener listener : listeners) {
            listener.onGlobalError(pageRunner, e);
        }
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (CompleteListener listener : listeners) {
            listener.onBeforePageAction(pageRunner, action);
        }
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (CompleteListener listener : listeners) {
            listener.onAfterPageAction(pageRunner, action);
        }
    }
}
