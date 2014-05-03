package net.mindengine.galen.validation;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;

public class CombinedValidationListener implements ValidationListener {

    
    private List<ValidationListener> listeners = new LinkedList<ValidationListener>();
    
    
    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (ValidationListener listener: listeners) {
            listener.onObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        for (ValidationListener listener: listeners) {
            listener.onAfterObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        for (ValidationListener listener: listeners) {
            listener.onSpecError(pageRunner, pageValidation, objectName, spec, error);
        }
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        for (ValidationListener listener: listeners) {
            listener.onSpecSuccess(pageRunner, pageValidation, objectName, spec);
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        for (ValidationListener listener: listeners) {
            listener.onGlobalError(pageRunner, e);
        }
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (ValidationListener listener: listeners) {
            listener.onBeforePageAction(pageRunner, action);
        }
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        for (ValidationListener listener: listeners) {
            listener.onAfterPageAction(pageRunner, action);
        }
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (ValidationListener listener: listeners) {
            listener.onBeforeSection(pageRunner, pageValidation, pageSection);
        }
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        for (ValidationListener listener: listeners) {
            listener.onAfterSection(pageRunner, pageValidation, pageSection);
        }
    }

    public void add(ValidationListener validationListener) {
        if (validationListener != null) {
            listeners.add(validationListener);
        }
    }

}
