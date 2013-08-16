package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.BrowserFactory;
import net.mindengine.galen.validation.ValidationError;


public class GalenSuiteRunner {

    private static final LinkedList<ValidationError> EMPTY_ERRORS = new LinkedList<ValidationError>();
    private SuiteListener suiteListener;
    private BrowserFactory browserFactory;
    
    private String name;

    public GalenSuiteRunner(BrowserFactory browserFactory) {
        this.setBrowserFactory(browserFactory);
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

    public void runSuite(List<GalenPageRunner> pageRunners) {
        if (pageRunners == null) {
            throw new IllegalArgumentException("Suite can not be null");
        }
        else if (pageRunners.size() == 0) {
            throw new IllegalArgumentException("Nothing to run. Suite is empty");
        }
        
        
        Browser browser = browserFactory.openBrowser();
        tellSuiteStarted();
        
        for (GalenPageRunner pageRunner : pageRunners) {
            tellBeforePage(pageRunner, browser);
            List<ValidationError> errors = runPage(pageRunner, browser);
            tellAfterPage(pageRunner, browser, errors);
        }
        
        tellSuiteFinished();
        
        browser.quit();
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

    private List<ValidationError> runPage(GalenPageRunner pageRunner, Browser browser) {
        try {
            return pageRunner.run(browser);
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

    public BrowserFactory getBrowserFactory() {
        return browserFactory;
    }

    public void setBrowserFactory(BrowserFactory browserFactory) {
        this.browserFactory = browserFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
