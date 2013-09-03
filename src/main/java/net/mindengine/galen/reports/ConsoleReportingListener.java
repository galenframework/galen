package net.mindengine.galen.reports;

import java.io.PrintStream;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class ConsoleReportingListener implements SuiteListener, ValidationListener{

    private static final String OBJECT_INDETATION = "    ";
    private static final String SPEC_ERROR_INDENTATION = "->      ";
    private static final String SPEC_ERROR_MESSAGE_INDENTATION = "            - ";
    private static final String SPEC_INDENTATION = "        ";
    private PrintStream out;
    private PrintStream err;

    public ConsoleReportingListener(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        out.print(OBJECT_INDETATION);
        out.println(objectName);
    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        err.print(SPEC_ERROR_INDENTATION);
        err.println(spec.toText());
        for(String message : error.getMessages()) {
            err.print(SPEC_ERROR_MESSAGE_INDENTATION);
            err.println(message);
        }
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        out.print(SPEC_INDENTATION);
        out.println(spec.toText());
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser,
            List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser) {
        out.println("----------------------------------------");
        out.print("Page: ");
        out.print(browser.getUrl());
        out.print(" ");
        out.println(GalenUtils.formatScreenSize(browser.getScreenSize()));
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        out.println("========================================");
        out.print("Suite: ");
        out.println(suite.getName());
        out.println("========================================");
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
        out.println();
    }

}
