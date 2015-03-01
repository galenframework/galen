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
package net.mindengine.galen.validation;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombinedValidationListener implements ValidationListener {

    private final static Logger LOG = LoggerFactory.getLogger(CombinedValidationListener.class);

    private List<ValidationListener> listeners = new LinkedList<ValidationListener>();
    
    
    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (ValidationListener listener : listeners) {
            try {
                listener.onObject(pageRunner, pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onAfterObject(pageRunner, pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onSpecError(pageRunner, pageValidation, objectName, spec, result);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }


    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onSpecSuccess(pageRunner, pageValidation, objectName, spec, result);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onGlobalError(pageRunner, e);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onBeforePageAction(pageRunner, action);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onAfterPageAction(pageRunner, action);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onBeforeSection(pageRunner, pageValidation, pageSection);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onAfterSection(pageRunner, pageValidation, pageSection);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onSubLayout(PageValidation pageValidation, String objectName) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onSubLayout(pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onAfterSubLayout(PageValidation pageValidation, String objectName) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onAfterSubLayout(pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }

    }

    @Override
    public void onSpecGroup(PageValidation pageValidation, String specGroupName) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onSpecGroup(pageValidation, specGroupName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }
    }

    @Override
    public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {
        for (ValidationListener listener: listeners) {
            try {
                listener.onAfterSpecGroup(pageValidation, specGroupName);
            }
            catch (Exception ex) {
                LOG.error("Unknown error during finishing test", ex);
            }
        }

    }

    public void add(ValidationListener validationListener) {
        if (validationListener != null) {
            listeners.add(validationListener);
        }
    }

}
