package net.mindengine.galen.tests;

import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;

public abstract class SuiteNameOnlyListener implements CompleteListener {

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner,
            PageValidation pageValidation, String objectName) {
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner,
            PageValidation pageValidation, String objectName, Spec spec,
            ValidationError error) {
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner,
            PageValidation pageValidation, String objectName, Spec spec) {
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner,
            GalenPageAction action) {
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner,
            GalenPageAction action) {
    }

    @Override
    public void onBeforeSection(GalenPageRunner pageRunner,
            PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner,
            PageValidation pageValidation, PageSection pageSection) {
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner,
            GalenPageRunner pageRunner, GalenPageTest pageTest,
            Browser browser, List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner,
            GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner,
            GalenSuite suite) {
    }

    @Override
    public abstract void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite);
    
    @Override
    public void done() {
    }

}
