package net.mindengine.galen.suite;

import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public interface GalenPageAction {
    
    List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception;
}
