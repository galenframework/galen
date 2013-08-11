package net.mindengine.galen.components;

import java.util.List;

import org.openqa.selenium.WebDriver;

import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.validation.ValidationError;

public class RecordingSuiteListener implements SuiteListener {

    StringBuffer recorded = new StringBuffer();
    
    public String getRecordedInvokations() {
        return recorded.toString();
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, WebDriver driver, List<ValidationError> errors) {
        record("<after-page errors=" + errors.size() + ">");
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, WebDriver driver) {
        record("<before-page>");
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner) {
        record("<suite-finished>");
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner) {
        record("<suite-started>");
    }

    private void record(String msg) {
        recorded.append(msg);
    }

}
