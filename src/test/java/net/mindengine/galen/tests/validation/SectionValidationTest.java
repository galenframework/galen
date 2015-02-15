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
package net.mindengine.galen.tests.validation;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.components.MockedBrowser;
import net.mindengine.galen.components.validation.MockedInvisiblePageElement;
import net.mindengine.galen.components.validation.MockedPage;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.SectionFilter;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class SectionValidationTest {


    private static final SectionFilter EMPTY_SECTION_FILTER = new SectionFilter(Collections.<String>emptyList(), Collections.<String>emptyList());

    @Test
    public void shouldMatch_onlyDigits_whenHashIsUsed_inObjectNamePattern() throws IOException {
        MockedPage page = new MockedPage(new HashMap<String, PageElement>(){{
            put("item-1", new MockedInvisiblePageElement(0, 0, 0, 0));
            put("item-342", new MockedInvisiblePageElement(0, 0, 0, 0));
        }});

        Browser browser = new MockedBrowser("", new Dimension(1024, 768), page);


        List<String> validatedObjectNames = new LinkedList<String>();
        ValidationListener validationListener = createRecordingListenerForObjectNames(validatedObjectNames);


        PageSpec pageSpec = readPageSpec("/specs/hash-in-object-name.spec");
        PageValidation pageValidation = new PageValidation(browser, page, pageSpec, validationListener, EMPTY_SECTION_FILTER);
        SectionValidation sectionValidation = new SectionValidation(pageSpec.getSections(), pageValidation, validationListener);

        sectionValidation.check();


        assertThat("Validated object name list should have size", validatedObjectNames.size(), is(2));
        assertThat("Validated object name list should be", validatedObjectNames, hasItems("item-1", "item-342"));

    }

    private ValidationListener createRecordingListenerForObjectNames(final List<String> validatedObjectNames) {
        return new ValidationListener() {
            @Override
            public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
                validatedObjectNames.add(objectName);
            }

            @Override
            public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {

            }

            @Override
            public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {

            }

            @Override
            public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {

            }

            @Override
            public void onGlobalError(GalenPageRunner pageRunner, Exception e) {

            }

            @Override
            public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {

            }

            @Override
            public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {

            }

            @Override
            public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {

            }

            @Override
            public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {

            }

            @Override
            public void onSubLayout(PageValidation pageValidation, String objectName) {

            }

            @Override
            public void onAfterSubLayout(PageValidation pageValidation, String objectName) {

            }
        };

    }

    private PageSpec readPageSpec(String specPath) throws IOException {
        return new PageSpecReader(new Properties(), null).read(specPath);
    }
}
