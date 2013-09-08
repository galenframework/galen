package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;


public class GalenSuiteRunner {

    private static final LinkedList<ValidationError> EMPTY_ERRORS = new LinkedList<ValidationError>();
    private SuiteListener suiteListener;
    private ValidationListener validationListener;
    
    public GalenSuiteRunner() {
    }

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

    
    public void runSuite(GalenSuite suite) {
        if (suite == null) {
            throw new IllegalArgumentException("Suite can not be null");
        }
        
        List<GalenPageTest> pageTests = suite.getPageTests();
        
        tellSuiteStarted(suite);
        
        GalenPageRunner pageRunner = new GalenPageRunner();
        pageRunner.setValidationListener(validationListener);
        
        for (GalenPageTest pageTest : pageTests) {
            Browser browser = pageTest.getBrowserFactory().openBrowser();
            
            tellBeforePage(pageTest, browser);
            List<ValidationError> errors = runPageTest(pageRunner, pageTest, browser);
            tellAfterPage(pageTest, browser, errors);
            
            browser.quit();
        }
        
        tellSuiteFinished(suite);
    }

    private void tellAfterPage(GalenPageTest pageTest, Browser browser, List<ValidationError> errors) {
        try {
            if (suiteListener != null) {
                suiteListener.onAfterPage(this, pageTest, browser, errors);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellBeforePage(GalenPageTest pageTest, Browser browser) {
        try {
            if (suiteListener != null) {
                suiteListener.onBeforePage(this, pageTest, browser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ValidationError> runPageTest(GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        try {
            return pageRunner.run(browser, pageTest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_ERRORS;
    }

    private void tellSuiteFinished(GalenSuite suite) {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteFinished(this, suite);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellSuiteStarted(GalenSuite suite) {
        try {
            if (suiteListener != null) {
                suiteListener.onSuiteStarted(this, suite);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }


}
