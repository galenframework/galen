package net.mindengine.galen.runner;

import java.util.List;

import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.WebDriver;

public interface SuiteListener {

    void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, WebDriver driver, List<ValidationError> errors);

    void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, WebDriver driver);

    void onSuiteFinished(GalenSuiteRunner galenSuiteRunner);

    void onSuiteStarted(GalenSuiteRunner galenSuiteRunner);

}
