/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation.specs;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.galenframework.browser.Browser;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.SpecComponent;
import com.galenframework.validation.*;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.SectionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

public class SpecValidationComponent extends SpecValidation<SpecComponent> {
    private final static Logger LOG = LoggerFactory.getLogger(SpecValidationComponent.class);
    private static final Map<String, Locator> NO_OBJECTS = Collections.emptyMap();

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecComponent spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        tellListenerSubLayout(pageValidation, objectName);
        List<ValidationResult> results;

        if (spec.isFrame()) {
            results = checkInsideFrame(mainObject, pageValidation, spec);
        }
        else {
            results = checkInsideNormalWebElement(pageValidation, objectName, spec);
        }
        tellListenerAfterSubLayout(pageValidation, objectName);

        List<ValidationObject> objects = asList(new ValidationObject(mainObject.getArea(), objectName));

        List<ValidationResult> errorResults = ValidationResult.filterOnlyErrorResults(results);
        if (errorResults.size() > 0) {
            throw new ValidationErrorException("Child component spec contains " + errorResults.size() + " errors")
                    .withValidationObjects(objects)
                    .withChildValidationResults(errorResults);
        }

        return new ValidationResult(spec, objects);
    }

    private void tellListenerAfterSubLayout(PageValidation pageValidation, String objectName) {
        if (pageValidation.getValidationListener() != null) {
            try {
                pageValidation.getValidationListener().onAfterSubLayout(pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.trace("Unknown error during validation after object", ex);
            }
        }
    }

    private void tellListenerSubLayout(PageValidation pageValidation, String objectName) {
        if (pageValidation.getValidationListener() != null) {
            try {
                pageValidation.getValidationListener().onSubLayout(pageValidation, objectName);
            }
            catch (Exception ex) {
                LOG.trace("Unknown error during validation after object", ex);
            }
        }
    }


    private List<ValidationResult> checkInsideFrame(PageElement mainObject, PageValidation pageValidation, SpecComponent spec) {
        Page page = pageValidation.getPage();

        Page framePage = page.createFrameContext(mainObject);

        List<ValidationResult> results = checkInsidePage(pageValidation.getBrowser(), framePage, spec,
                pageValidation.getSectionFilter(), pageValidation.getValidationListener());

        if (spec.isFrame()) {
            page.switchToParentFrame();
        }

        return results;
    }


    private List<ValidationResult> checkInsidePage(Browser browser, Page page, SpecComponent spec,
                                                   SectionFilter sectionFilter, ValidationListener validationListener) {
        PageSpecReader pageSpecReader = new PageSpecReader();

        PageSpec componentPageSpec;
        try {
            componentPageSpec = pageSpecReader.read(spec.getSpecPath(),
                    page, sectionFilter, spec.getProperties(),
                    wrapJsVariables(spec.getJsVariables(), spec.getArguments()),
                    NO_OBJECTS
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not read spec " + spec.getSpecPath(), e);
        }

        SectionValidation sectionValidation = new SectionValidation(componentPageSpec.getSections(),
                new PageValidation(browser, page, componentPageSpec, validationListener, sectionFilter),
                validationListener);

        return sectionValidation.check();
    }

    private Map<String, Object> wrapJsVariables(Map<String, Object> jsVariables, Map<String, Object> arguments) {
        Map<String, Object> newJsVariables = new HashMap<>();
        if (jsVariables != null) {
            newJsVariables.putAll(jsVariables);
        }

        if (arguments != null) {
            newJsVariables.putAll(arguments);
        }

        return newJsVariables;
    }

    private List<ValidationResult> checkInsideNormalWebElement(PageValidation pageValidation, String objectName, SpecComponent spec) {
        Locator mainObjectLocator = pageValidation.getPageSpec().getObjectLocator(objectName);
        Page objectContextPage = pageValidation.getPage().createObjectContextPage(mainObjectLocator);

        return checkInsidePage(pageValidation.getBrowser(), objectContextPage, spec,
                pageValidation.getSectionFilter(), pageValidation.getValidationListener());
    }

}
