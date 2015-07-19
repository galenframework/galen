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
package com.galenframework.api;

import com.galenframework.browser.Browser;
import com.galenframework.page.Rect;
import com.galenframework.speclang2.reader.pagespec.PageSpecReader;
import com.galenframework.validation.*;
import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.reports.LayoutReportListener;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.speclang2.reader.pagespec.PageSpecReader;
import com.galenframework.specs.reader.page.PageSpec;
import com.galenframework.specs.reader.page.SectionFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties,
                                           Map<String, Object> jsVariables,
                                           File screenshotFile) throws IOException {
        return checkLayout(browser, specPath, includedTags, excludedTags, properties, jsVariables, screenshotFile, null);
    }

    public static LayoutReport checkLayout(Browser browser, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, Map<String, Object> jsVariables,
                                           File screenshotFile, ValidationListener validationListener) throws IOException {
        PageSpecReader reader = new PageSpecReader();
        PageSpec pageSpec = reader.read(specPath, browser.getPage(), includedTags, excludedTags, properties, jsVariables);
        return checkLayout(browser, pageSpec, includedTags, excludedTags, screenshotFile, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, PageSpec pageSpec,
                                           List<String> includedTags, List<String> excludedTags,
                                           ValidationListener validationListener) throws IOException {
        return checkLayout(browser, pageSpec, includedTags, excludedTags, EMPTY_SCREENSHOT_FILE, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, PageSpec pageSpec,
                                   List<String> includedTags, List<String> excludedTags,
                                   File screenshotFile, ValidationListener validationListener) throws IOException {

        Page page = browser.getPage();
        page.setScreenshot(screenshotFile);

        return checkLayoutForPage(page, browser, pageSpec, includedTags, excludedTags, validationListener);
    }

    private static LayoutReport checkLayoutForPage(Page page, Browser browser, PageSpec pageSpec,
                                           List<String> includedTags, List<String> excludedTags,
                                           ValidationListener validationListener) throws IOException {

        CombinedValidationListener listener = new CombinedValidationListener();
        listener.add(validationListener);

        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setIncludedTags(includedTags);
        layoutReport.setExcludedTags(excludedTags);
        try {
            File screenshot = page.createScreenshot();
            if (screenshot != null) {
                layoutReport.setScreenshot(layoutReport.registerFile("screenshot.png", screenshot));
            }
        }
        catch (Exception ex) {
            LOG.error("Error during setting screenshot.", ex);

        }
        listener.add(new LayoutReportListener(layoutReport));

        SectionFilter sectionFilter = new SectionFilter(includedTags, excludedTags);
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
        return checkLayout(driver, spec, includedTags, EMPTY_TAGS, EMPTY_PROPERTIES, EMPTY_VARS, EMPTY_SCREENSHOT_FILE, EMPTY_VALIDATION_LISTENER);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, EMPTY_VARS, EMPTY_SCREENSHOT_FILE);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, Map<String, Object> jsVariables) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, jsVariables, EMPTY_SCREENSHOT_FILE);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, Map<String, Object> jsVariables,
                                           File screenshotFile) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, jsVariables, screenshotFile);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, Map<String, Object> jsVariables, File screenshotFile, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, jsVariables, screenshotFile, validationListener);
    }

    public static void dumpPage(WebDriver driver, String pageName, String specPath, String pageDumpPath) throws IOException {
        dumpPage(driver, pageName, specPath, pageDumpPath, null, null, false);
    }

    public static void dumpPage(WebDriver driver, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Boolean onlyImages) throws IOException {
        dumpPage(new SeleniumBrowser(driver), pageName, specPath, pageDumpPath, maxWidth, maxHeight, onlyImages);
    }

    public static void dumpPage(WebDriver driver, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Boolean onlyImages, Map<String, Object> jsVariables) throws IOException {
        dumpPage(new SeleniumBrowser(driver), pageName, specPath, pageDumpPath, maxWidth, maxHeight, onlyImages, jsVariables);
    }

    public static void dumpPage(Browser browser, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Boolean onlyImages) throws IOException {
        dumpPage(browser, pageName, specPath, pageDumpPath, maxWidth, maxHeight, onlyImages, new Properties(), null);
    }

    public static void dumpPage(Browser browser, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Boolean onlyImages, Map<String, Object> jsVariables) throws IOException {
        dumpPage(browser, pageName, specPath, pageDumpPath, maxWidth, maxHeight, onlyImages, new Properties(), jsVariables);
    }
    public static void dumpPage(Browser browser, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Boolean onlyImages, Properties properties, Map<String, Object> jsVariables) throws IOException {
        PageSpecReader reader = new PageSpecReader();

        PageSpec pageSpec = reader.read(specPath, browser.getPage(), EMPTY_TAGS, EMPTY_TAGS, properties, jsVariables);
        dumpPage(browser, pageName, pageSpec, new File(pageDumpPath), maxWidth, maxHeight, onlyImages);
    }

    public static void dumpPage(Browser browser, String pageName, PageSpec pageSpec, File reportFolder, Integer maxWidth, Integer maxHeight, boolean onlyImages) throws IOException {
        if (!reportFolder.exists()) {
            if (!reportFolder.mkdirs()) {
                throw new RuntimeException("Cannot create dir: " + reportFolder.getAbsolutePath());
            }
        }


        Set<String> objectNames = pageSpec.getObjects().keySet();
        PageValidation pageValidation = new PageValidation(browser, browser.getPage(), pageSpec, null, null);

        PageDump pageDump = new PageDump();
        pageDump.setTitle(browser.getPage().getTitle());
        for (String objectName : objectNames) {
            PageElement pageElement = pageValidation.findPageElement(objectName);

            if (pageElement.isVisible() && pageElement.getArea() != null) {
                PageDump.Element element = new PageDump.Element(objectName, pageElement.getArea().toIntArray(), pageElement.getText());

                if (pageElement.isPresent() && pageElement.isVisible() && isWithinArea(pageElement, maxWidth, maxHeight)) {
                    element.setHasImage(true);
                }
                pageDump.addElement(element);
            }
        }

        if (!onlyImages) {
            pageDump.setPageName(pageName);
            pageDump.exportAsJson(new File(reportFolder.getAbsoluteFile() + File.separator + "page.json"));
            pageDump.exportAsHtml(pageName, new File(reportFolder.getAbsoluteFile() + File.separator + "page.html"));
            copyResource("/html-report/jquery-1.11.2.min.js", new File(reportFolder.getAbsolutePath() + File.separator + "jquery-1.11.2.min.js"));
            copyResource("/pagedump/galen-pagedump.js", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.js"));
            copyResource("/pagedump/galen-pagedump.css", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.css"));
        }

        pageDump.exportAllScreenshots(browser, reportFolder);
    }

    private static void copyResource(String resourceName, File destFile) throws IOException {
        String value = IOUtils.toString(Galen.class.getResourceAsStream(resourceName));
        FileUtils.writeStringToFile(destFile, value);
    }

    private static boolean isWithinArea(PageElement element, Integer maxWidth, Integer maxHeight) {
        Rect area = element.getArea();
        if (maxWidth != null && maxHeight != null) {
            return maxWidth * maxHeight > area.getWidth() * area.getHeight();
        }
        else if (maxWidth != null) {
            return maxWidth > area.getWidth();
        }
        else if (maxHeight != null) {
            return maxHeight > area.getHeight();
        }
        else return true;
    }

}
