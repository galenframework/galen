/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.junit;

import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumBrowserFactory;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationListener;
import com.galenframework.validation.ValidationResult;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.List;

import static com.galenframework.api.Galen.checkLayout;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;

/**
 * Performs a single page test using JUnit. Have a look at the following
 * example
 * <pre>
 * &#064;RunWith(GalenSpecRunner.class)
 * &#064;Exclude("firstExclude", "secondExclude")
 * &#064;Include("firstInclude", "secondInclude")
 * &#064;Size(width=640, height=480)
 * &#064;Spec("/my/package/homepage.gspec")
 * &#064;Url("http://localhost:13728/")
 * public void SinglePageTest {
 *
 * }
 * </pre>
 * <p>This test performs a single page test for the URL
 * {@code http://localhost:13728/}. It starts the browser, sets its size to
 * {@code 640x480} and verifies that the page fulfills the specification
 * {@code homepage.spec}. It excludes the sections {@code firstExclude} and
 * {@code secondExclude} of the specification and includes the sections
 * {@code firstInclude} and {@code secondInclude}.
 * <p>The annotations {@code &#064;Size}, {@code &#064;Spec} and
 * {@code &#064;Url} are mandatory.
 */
public class GalenSpecRunner extends Runner {
    private static final Map<String, Object> NO_JS_VARIABLES = emptyMap();
    private static final Properties NO_PROPERTIES = new Properties();
    private static final File NO_SCREENSHOT = null;
    private static final List<String> NO_TAGS = emptyList();
    private Class<?> testClass;

    /**
     * Constructs a new {@code GalenSpecRunner} that will run {@code testClass}.
     *
     * @param testClass the class with the test specification.
     */
    public GalenSpecRunner(Class<?> testClass) throws InitializationError {
        this.testClass = testClass;
    }

    @Override
    public Description getDescription() {
        return createSuiteDescription(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            Dimension windowsSize = getWindowSize();
            String specPath = getSpecPath();
            String pageUrl = getUrl();
            SectionFilter sectionFilter = getSectionFilter();
            run(notifier, windowsSize, sectionFilter, specPath, pageUrl);
        } catch (Throwable e) {
            Failure failure = new Failure(getDescription(), e);
            notifier.fireTestFailure(failure);
        }
    }

    private void run(RunNotifier notifier, Dimension windowsSize, SectionFilter sectionFilter, String specPath,
            String pageUrl) throws IOException {
        JUnitListener listener = new JUnitListener(notifier);
        run(listener, windowsSize, sectionFilter, specPath, pageUrl);
    }

    private void run(JUnitListener listener, Dimension windowsSize, SectionFilter sectionFilter, String specPath,
            String url) throws IOException {
        Browser browser = createBrowser();
        try {
            browser.load(url);
            browser.changeWindowSize(windowsSize);
            checkLayout(browser, specPath, sectionFilter, NO_PROPERTIES, NO_JS_VARIABLES, NO_SCREENSHOT, listener);
        } finally {
            browser.quit();
        }
    }

    private Browser createBrowser() {
        return new SeleniumBrowserFactory().openBrowser();
    }

    private SectionFilter getSectionFilter() {
        return new SectionFilter(getIncludedTags(), getExcludedTags());
    }

    private List<String> getExcludedTags() {
        Exclude annotation = testClass.getAnnotation(Exclude.class);
        return annotation == null ? NO_TAGS : asList(annotation.value());
    }

    private List<String> getIncludedTags() {
        Include annotation = testClass.getAnnotation(Include.class);
        return annotation == null ? NO_TAGS : asList(annotation.value());
    }

    private String getSpecPath() {
        return getMandatoryAnnotation(Spec.class).value();
    }

    private String getUrl() {
        return getMandatoryAnnotation(Url.class).value();
    }

    private Dimension getWindowSize() {
        Size size = getMandatoryAnnotation(Size.class);
        return new Dimension(size.width(), size.height());
    }

    private <A extends Annotation> A getMandatoryAnnotation(Class<A> annotationType) {
        A annotation = testClass.getAnnotation(annotationType);
        if (annotation == null) {
            throw new IllegalStateException("The annotation @"
                    + annotationType.getSimpleName() + " is missing.");
        } else {
            return annotation;
        }
    }

    private static class JUnitListener implements ValidationListener {
        private final RunNotifier runNotifier;

        public JUnitListener(RunNotifier runNotifier) {
            this.runNotifier = runNotifier;
        }

        @Override
        public void onObject(PageValidation pageValidation, String objectName) {

        }

        @Override
        public void onAfterObject(PageValidation pageValidation, String objectName) {

        }

        @Override
        public void onBeforeSpec(PageValidation pageValidation, String objectName, com.galenframework.specs.Spec spec) {
            Description description = createDescriptionForSpec(objectName, spec);
            runNotifier.fireTestStarted(description);
        }

        @Override
        public void onSpecError(PageValidation pageValidation, String objectName, com.galenframework.specs.Spec spec, ValidationResult validationResult) {
            Description description = createDescriptionForSpec(objectName, spec);
            Failure failure = new Failure(description, new AssertionError(validationResult.getError().getMessages()));
            runNotifier.fireTestFailure(failure);
            runNotifier.fireTestFinished(description);
        }

        @Override
        public void onSpecSuccess(PageValidation pageValidation, String objectName, com.galenframework.specs.Spec spec, ValidationResult validationResult) {
            Description description = createDescriptionForSpec(objectName, spec);
            runNotifier.fireTestFinished(description);
        }

        private Description createDescriptionForSpec(String objectName, com.galenframework.specs.Spec spec) {
            return createTestDescription(objectName, spec.getOriginalText());
        }

        @Override
        public void onGlobalError(Exception e) {

        }

        @Override
        public void onBeforePageAction(GalenPageAction action) {

        }

        @Override
        public void onAfterPageAction(GalenPageAction action) {

        }

        @Override
        public void onBeforeSection(PageValidation pageValidation, PageSection pageSection) {

        }

        @Override
        public void onAfterSection(PageValidation pageValidation, PageSection pageSection) {

        }

        @Override
        public void onSubLayout(PageValidation pageValidation, String objectName) {

        }

        @Override
        public void onAfterSubLayout(PageValidation pageValidation, String objectName) {

        }

        @Override
        public void onSpecGroup(PageValidation pageValidation, String specGroupName) {

        }

        @Override
        public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {

        }
    }
}
