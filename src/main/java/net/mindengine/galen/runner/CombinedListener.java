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

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

public class CombinedListener implements CompleteListener {

    List<CompleteListener> listeners = new LinkedList<CompleteListener>();
    
    public void add(CompleteListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void onTestFinished(GalenTest test) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onTestFinished(test);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onTestStarted(GalenTest test) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onTestStarted(test);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onObject(pageRunner, pageValidation, objectName);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onAfterObject(pageRunner, pageValidation, objectName);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onSpecError(pageRunner, pageValidation, objectName, spec, error);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onSpecSuccess(pageRunner, pageValidation, objectName, spec);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void done() {
        for (CompleteListener listener : listeners) {
            try {
                listener.done();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onGlobalError(pageRunner, e);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onBeforePageAction(pageRunner, action);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onAfterPageAction(pageRunner, action);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onBeforeSection(pageRunner, pageValidation, pageSection);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (CompleteListener listener : listeners) {
            try {
                listener.onAfterSection(pageRunner, pageValidation, pageSection);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void beforeTestSuite(List<GalenTest> tests) {
        for (CompleteListener listener : listeners) {
            try {
                listener.beforeTestSuite(tests);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void afterTestSuite(List<GalenTestInfo> tests) {
        for (CompleteListener listener : listeners) {
            try {
                listener.afterTestSuite(tests);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
