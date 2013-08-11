package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GalenSuiteRunner {

    private static final LinkedList<ValidationError> EMPTY_ERRORS = new LinkedList<ValidationError>();
    private SuiteListener suiteListener;

    public GalenSuiteRunner withSuiteListener(SuiteListener suiteListener) {
        this.setSuiteListener(suiteListener);
        return this;
    }

    public SuiteListener getSuiteListener() {
        return suiteListener;
    }

    public void setSuiteListener(SuiteListener suiteListener) {
        this.suiteListener = suiteListener;
    }

    public void runSuite(List<GalenPageRunner> pageRunners) {
        if (pageRunners == null) {
            throw new IllegalArgumentException("Suite can not be null");
        }
        else if (pageRunners.size() == 0) {
            throw new IllegalArgumentException("Nothing to run. Suite is empty");
        }
        
        WebDriver driver = new FirefoxDriver();
        tellSuiteStarted();
        
        for (GalenPageRunner pageRunner : pageRunners) {
            tellBeforePage(pageRunner, driver);
            List<ValidationError> errors = runPage(pageRunner, driver);
            tellAfterPage(pageRunner, driver, errors);
        }
        
        tellSuiteFinished();
        
        driver.quit();
    }

    private void tellAfterPage(GalenPageRunner pageRunner, WebDriver driver, List<ValidationError> errors) {
        try {
            if (suiteListener != null) {
                suiteListener.onAfterPage(this, pageRunner, driver, errors);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellBeforePage(GalenPageRunner pageRunner, WebDriver driver) {
        try {
            if (suiteListener != null) {
                suiteListener.onBeforePage(this, pageRunner, driver);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ValidationError> runPage(GalenPageRunner pageRunner, WebDriver driver) {
        try {
            return pageRunner.run(driver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_ERRORS;
    }

    private void tellSuiteFinished() {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteFinished(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellSuiteStarted() {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteStarted(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
