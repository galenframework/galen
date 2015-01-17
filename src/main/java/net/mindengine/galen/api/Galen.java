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
package net.mindengine.galen.api;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.SectionFilter;
import net.mindengine.galen.validation.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

public class Galen {

    private final static Logger LOG = LoggerFactory.getLogger(Galen.class);

    public static LayoutReport checkLayout(Browser browser, List<String> specPaths,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        PageSpecReader reader = new PageSpecReader(properties, browser.getPage());

        List<PageSpec> specs = new LinkedList<PageSpec>();

        for (String specPath : specPaths) {
            specs.add(reader.read(specPath));
        }

        return checkLayout(browser, specs, includedTags, excludedTags, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, List<PageSpec> specs,
                                   List<String> includedTags, List<String> excludedTags,
                                   ValidationListener validationListener) throws IOException {

        Page page = browser.getPage();

        CombinedValidationListener listener = new CombinedValidationListener();
        listener.add(validationListener);

        LayoutReport layoutReport = new LayoutReport();
        try {
            final File screenshot = page.createScreenshot();
            if (screenshot != null) {
                layoutReport.setScreenshotFullPath(screenshot.getAbsolutePath());
            }
        }
        catch (Exception ex) {
            LOG.error("Error during setting screenshot.", ex);
        
        }
        listener.add(new LayoutReportListener(layoutReport));


        List<ValidationError> allValidationErrors = new LinkedList<ValidationError>();

        for (PageSpec spec : specs) {

            SectionFilter sectionFilter = new SectionFilter(includedTags, excludedTags);
            List<PageSection> pageSections = spec.findSections(sectionFilter);
            SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(browser, page, spec, listener, sectionFilter), listener);

            List<ValidationError> errors = sectionValidation.check();
            if (errors != null && errors.size() > 0) {
                allValidationErrors.addAll(errors);
            }
        }

        layoutReport.setValidationErrors(allValidationErrors);

        return layoutReport;
    }

    public static LayoutReport checkLayout(WebDriver driver, List<String> specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, validationListener);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), asList(specPath), includedTags, excludedTags, properties, validationListener);
    }

    public static void dumpPage(WebDriver driver, String pageName, String specPath, String pageDumpPath) throws IOException {
        dumpPage(driver, pageName, specPath, pageDumpPath, null, null);
    }

    public static void dumpPage(WebDriver driver, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight) throws IOException {
        dumpPage(new SeleniumBrowser(driver), pageName, specPath, pageDumpPath, maxWidth, maxHeight);
    }

    public static void dumpPage(Browser browser, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight) throws IOException {
        dumpPage(browser, pageName, specPath, pageDumpPath, maxWidth, maxHeight, new Properties());
    }
    public static void dumpPage(Browser browser, String pageName, String specPath, String pageDumpPath, Integer maxWidth, Integer maxHeight, Properties properties) throws IOException {
        PageSpecReader reader = new PageSpecReader(properties, browser.getPage());
        PageSpec pageSpec = reader.read(specPath);
        dumpPage(browser, pageName, pageSpec, new File(pageDumpPath), maxWidth, maxHeight);
    }

    public static void dumpPage(Browser browser, String pageName, PageSpec pageSpec, File reportFolder, Integer maxWidth, Integer maxHeight) throws IOException {
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

        pageDump.setPageName(pageName);
        pageDump.exportAsJson(new File(reportFolder.getAbsoluteFile() + File.separator + "page.json"));
        pageDump.exportAsHtml(pageName, new File(reportFolder.getAbsoluteFile() + File.separator + "page.html"));
        pageDump.exportAllScreenshots(browser, reportFolder);


        copyResource("/html-report/jquery-1.10.2.min.js", new File(reportFolder.getAbsolutePath() + File.separator + "jquery-1.10.2.min.js"));
        copyResource("/pagedump/galen-pagedump.js", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.js"));
        copyResource("/pagedump/galen-pagedump.css", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.css"));
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
