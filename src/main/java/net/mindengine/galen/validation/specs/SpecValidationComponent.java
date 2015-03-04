/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import java.io.IOException;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.SpecComponent;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.SectionFilter;
import net.mindengine.galen.validation.*;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

public class SpecValidationComponent extends SpecValidation<SpecComponent> {
    private final static Logger LOG = LoggerFactory.getLogger(SpecValidationComponent.class);

    @Override
    public ValidationResult check(final PageValidation pageValidation, final String objectName, final SpecComponent spec) throws ValidationErrorException {
        final PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        tellListenerSubLayout(pageValidation, objectName);
        List<ValidationResult> results;

        if (spec.isFrame()) {
            results = checkInsideFrame(mainObject, pageValidation, spec);
        } else {
            results = checkInsideNormalWebElement(pageValidation, objectName, spec);
        }
        tellListenerAfterSubLayout(pageValidation, objectName);

        final List<ValidationObject> objects = asList(new ValidationObject(mainObject.getArea(), objectName));

        final List<ValidationResult> errorResults = ValidationResult.filterOnlyErrorResults(results);
        if (CollectionUtils.isNotEmpty(errorResults)) {
            throw new ValidationErrorException("Child component spec contains " + errorResults.size() + " errors").withValidationObjects(objects);
        }

        return new ValidationResult(objects);
    }

    private void tellListenerAfterSubLayout(final PageValidation pageValidation, final String objectName) {
        if (pageValidation.getValidationListener() != null) {
            try {
                pageValidation.getValidationListener().onAfterSubLayout(pageValidation, objectName);
            } catch (final Exception ex) {
                LOG.trace("Unknown error during validation after object", ex);
            }
        }
    }

    private void tellListenerSubLayout(final PageValidation pageValidation, final String objectName) {
        if (pageValidation.getValidationListener() != null) {
            try {
                pageValidation.getValidationListener().onSubLayout(pageValidation, objectName);
            } catch (final Exception ex) {
                LOG.trace("Unknown error during validation after object", ex);
            }
        }
    }

    private List<ValidationResult> checkInsideFrame(final PageElement mainObject, final PageValidation pageValidation, final SpecComponent spec) {
        final Page page = pageValidation.getPage();

        final Page framePage = page.createFrameContext(mainObject);

        final List<ValidationResult> results = checkInsidePage(pageValidation.getBrowser(), framePage, spec, pageValidation.getSectionFilter(),
                pageValidation.getValidationListener());

        if (spec.isFrame()) {
            page.switchToParentFrame();
        }

        return results;
    }

    private List<ValidationResult> checkInsidePage(final Browser browser, final Page page, final SpecComponent spec, final SectionFilter sectionFilter,
            final ValidationListener validationListener) {
        final PageSpecReader pageSpecReader = new PageSpecReader(spec.getProperties(), page);

        PageSpec componentPageSpec;
        try {
            componentPageSpec = pageSpecReader.read(spec.getSpecPath());
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final SectionValidation sectionValidation = new SectionValidation(componentPageSpec.findSections(sectionFilter), new PageValidation(browser, page,
                componentPageSpec, validationListener, sectionFilter), validationListener);

        return sectionValidation.check();
    }

    private List<ValidationResult> checkInsideNormalWebElement(final PageValidation pageValidation, final String objectName, final SpecComponent spec) {
        final Locator mainObjectLocator = pageValidation.getPageSpec().getObjectLocator(objectName);
        final Page objectContextPage = pageValidation.getPage().createObjectContextPage(mainObjectLocator);

        return checkInsidePage(pageValidation.getBrowser(), objectContextPage, spec, pageValidation.getSectionFilter(), pageValidation.getValidationListener());
    }

}
