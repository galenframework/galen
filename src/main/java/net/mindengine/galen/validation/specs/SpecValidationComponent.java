package net.mindengine.galen.validation.specs;

import java.util.List;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.specs.SpecComponent;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationErrorException;
import net.mindengine.galen.validation.ValidationListener;

public class SpecValidationComponent extends SpecValidation<SpecComponent> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecComponent spec) throws ValidationErrorException {
        
        Page page = pageValidation.getPage();
        if (!(page instanceof SeleniumPage)) {
            throw new ValidationErrorException("Cannot perform component validations. Needs to be run in Selenium Browser");
        }
        
        Locator mainObjectLocator = pageValidation.getPageSpec().getObjectLocator(objectName);
        Page objectContextPage = page.createObjectContextPage(mainObjectLocator);
        
        ValidationListener validationListener = pageValidation.getValidationListener();
        
        //TODO pass tags filter to child section validation
        SectionValidation sectionValidation = new SectionValidation(spec.getPageSpec().getSections(), 
                new PageValidation(objectContextPage, spec.getPageSpec(), validationListener), 
                validationListener);
        
        List<ValidationError> errors = sectionValidation.check();
        if (errors != null && errors.size() > 0) {
            throw new ValidationErrorException("Child component spec contains " + errors.size() + " errors");
        }
    }

}
