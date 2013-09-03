package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.ValidationError;


public class GalenSuiteRunner {

    private static final LinkedList<ValidationError> EMPTY_ERRORS = new LinkedList<ValidationError>();
    private SuiteListener suiteListener;
    
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

    
    //TODO Write tests for suite runner
    public void runSuite(GalenSuite suite) {
        if (suite == null) {
            throw new IllegalArgumentException("Suite can not be null");
        }
        
        List<GalenPageTest> pageTests = suite.getPageTests();
        
        tellSuiteStarted(suite);
        
        GalenPageRunner pageRunner = new GalenPageRunner();
        
        for (GalenPageTest pageTest : pageTests) {
            Browser browser = pageTest.getBrowserFactory().openBrowser();
            
            tellBeforePage(pageRunner, browser);
            List<ValidationError> errors = runPageTest(pageRunner, pageTest, browser);
            tellAfterPage(pageRunner, browser, errors);
            
            browser.quit();
        }
        
        tellSuiteFinished(suite);
    }

    private void tellAfterPage(GalenPageRunner pageRunner, Browser browser, List<ValidationError> errors) {
        try {
            if (suiteListener != null) {
                suiteListener.onAfterPage(this, pageRunner, browser, errors);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellBeforePage(GalenPageRunner pageRunner, Browser browser) {
        try {
            if (suiteListener != null) {
                suiteListener.onBeforePage(this, pageRunner, browser);
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


}
