/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
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
        SectionValidation sectionValidation = new SectionValidation(spec.getPageSpec().findSections(pageValidation.getSectionFilter()), 
                new PageValidation(objectContextPage, spec.getPageSpec(), validationListener, pageValidation.getSectionFilter()), 
                validationListener);
        
        List<ValidationError> errors = sectionValidation.check();
        if (errors != null && errors.size() > 0) {
            throw new ValidationErrorException("Child component spec contains " + errors.size() + " errors");
        }
    }

}
