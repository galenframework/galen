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
package com.galenframework.api;

import com.galenframework.browser.Browser;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.validation.*;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.page.Page;
import com.galenframework.reports.LayoutReportListener;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.SectionFilter;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Galen {

    private final static Logger LOG = LoggerFactory.getLogger(Galen.class);
    private final static File EMPTY_SCREENSHOT_FILE = null;
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private static final ValidationListener EMPTY_VALIDATION_LISTENER = null;
    private static final List<String> EMPTY_TAGS = Collections.emptyList();
    private static final Map<String, Object> EMPTY_VARS = Collections.emptyMap();


    public static LayoutReport checkLayout(Browser browser, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties,
                                           Map<String, Object> jsVariables,
                                           File screenshotFile) throws IOException {
        return checkLayout(browser, specPath, sectionFilter, properties, jsVariables, screenshotFile, null);
    }
    public static LayoutReport checkLayout(Browser browser, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties, Map<String, Object> jsVariables,
                                           File screenshotFile, ValidationListener validationListener
                                           ) throws IOException {
        return checkLayout(browser, specPath, sectionFilter, properties, jsVariables, screenshotFile, validationListener, null);
    }

    public static LayoutReport checkLayout(Browser browser, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties, Map<String, Object> jsVariables,
                                           File screenshotFile, ValidationListener validationListener,
                                           Map<String, Locator> objects) throws IOException {
        PageSpecReader reader = new PageSpecReader();
        PageSpec pageSpec = reader.read(specPath, browser.getPage(), sectionFilter, properties, jsVariables, objects);
        return checkLayout(browser, pageSpec, sectionFilter, screenshotFile, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, PageSpec pageSpec,
                                           SectionFilter sectionFilter,
                                           ValidationListener validationListener) throws IOException {
        return checkLayout(browser, pageSpec, sectionFilter, EMPTY_SCREENSHOT_FILE, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, PageSpec pageSpec,
                                           SectionFilter sectionFilter,
                                           File screenshotFile,
                                           ValidationListener validationListener) throws IOException {

        Page page = browser.getPage();
        page.setScreenshot(screenshotFile);

        return checkLayoutForPage(page, browser, pageSpec, sectionFilter, validationListener);
    }

    private static LayoutReport checkLayoutForPage(Page page, Browser browser, PageSpec pageSpec,
                                                   SectionFilter sectionFilter,
                                                   ValidationListener validationListener) throws IOException {

        CombinedValidationListener listener = new CombinedValidationListener();
        listener.add(validationListener);

        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setIncludedTags(sectionFilter.getIncludedTags());
        layoutReport.setExcludedTags(sectionFilter.getExcludedTags());
        try {
            File screenshot = page.getScreenshotFile();
            if (screenshot != null) {
                layoutReport.setScreenshot(layoutReport.registerFile("screenshot.png", screenshot));
                screenshot.deleteOnExit();
            }
        }
        catch (Exception ex) {
            LOG.error("Error during setting screenshot.", ex);

        }
        listener.add(new LayoutReportListener(layoutReport));

        SectionValidation sectionValidation = new SectionValidation(pageSpec.getSections(), new PageValidation(browser, page, pageSpec, listener, sectionFilter), listener);

        List<ValidationResult> results = sectionValidation.check();
        List<ValidationResult> allValidationErrorResults = new LinkedList<>();

        for (ValidationResult result : results) {
            if (result.getError() != null) {
                allValidationErrorResults.add(result);
            }
        }

        layoutReport.setValidationErrorResults(allValidationErrorResults);

        return layoutReport;
    }

    public static LayoutReport checkLayout(WebDriver driver, String spec, List<String> includedTags) throws IOException {
        return checkLayout(driver, spec, new SectionFilter(includedTags, EMPTY_TAGS),
                EMPTY_PROPERTIES, EMPTY_VARS, EMPTY_SCREENSHOT_FILE, EMPTY_VALIDATION_LISTENER);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, sectionFilter, properties, EMPTY_VARS, EMPTY_SCREENSHOT_FILE);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties, Map<String, Object> jsVariables) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, sectionFilter, properties, jsVariables, EMPTY_SCREENSHOT_FILE);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties, Map<String, Object> jsVariables,
                                           File screenshotFile) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, sectionFilter, properties, jsVariables, screenshotFile);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           SectionFilter sectionFilter,
                                           Properties properties, Map<String, Object> jsVariables, File screenshotFile, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, sectionFilter, properties, jsVariables, screenshotFile, validationListener);
    }

}
