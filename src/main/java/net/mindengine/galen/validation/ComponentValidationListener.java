package net.mindengine.galen.validation;

import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;

public class ComponentValidationListener implements ValidationListener {

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        // TODO Auto-generated method stub

    }

}
