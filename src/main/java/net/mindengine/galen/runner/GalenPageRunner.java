package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;


public class GalenPageRunner {

    private ValidationListener validationListener;
    
    public GalenPageRunner withValidationListener(ValidationListener validationListener) {
        this.setValidationListener(validationListener);
        return this;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public List<ValidationError> run(Browser browser, GalenPageTest pageTest) {
        if (pageTest.getScreenSize() != null) {
            browser.changeWindowSize(pageTest.getScreenSize());
        }
        
        browser.load(pageTest.getUrl());
        
        List<ValidationError> allErrors = new LinkedList<ValidationError>();
        
        for (GalenPageAction action : pageTest.getActions()) {
            try {
                List<ValidationError> errors = action.execute(browser, pageTest, validationListener);
                if (errors != null) {
                    allErrors.addAll(errors);
                }
            }
            catch (Exception e) {
                allErrors.add(ValidationError.fromException(e));
            }
        }
        
        return allErrors;
    }

}
