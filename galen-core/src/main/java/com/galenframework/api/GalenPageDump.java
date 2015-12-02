/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.Rainbow4J;

import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.PageValidation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GalenPageDump {

    private final static Logger LOG = LoggerFactory.getLogger(GalenPageDump.class);
    private static final List<String> EMPTY_TAGS = Collections.emptyList();
    private boolean onlyImages = false;

    private String pageName;

    private Integer maxWidth = null;
    private Integer maxHeight = null;
    private Properties properties;
    private Map<String, Object> jsVariables;
    private Map<String, Locator> objects;
    private List<String> excludedObjects;

    public GalenPageDump(String pageName) {
        setPageName(pageName);
    }

    public void dumpPage(WebDriver driver, String pageSpecPath, String reportFolderPath) throws IOException {
        Browser browser = new SeleniumBrowser(driver);
        dumpPage(browser, loadPageSpec(browser, pageSpecPath), reportFolderPath);
    }

    private PageSpec loadPageSpec(Browser browser, String pageSpecPath) throws IOException {
        PageSpecReader reader = new PageSpecReader();
        return reader.read(pageSpecPath, browser.getPage(), new SectionFilter(EMPTY_TAGS, EMPTY_TAGS), properties, jsVariables, objects);
    }

    public void dumpPage(Browser browser, String pageSpecPath, String reportFolderPath) throws IOException {
        dumpPage(browser, loadPageSpec(browser, pageSpecPath), reportFolderPath);
    }
    public void dumpPage(Browser browser, PageSpec pageSpec, String reportFolderPath) throws IOException {
        File reportFolder = new File(reportFolderPath);
        if (!reportFolder.exists()) {
            if (!reportFolder.mkdirs()) {
                throw new RuntimeException("Cannot create dir: " + reportFolder.getAbsolutePath());
            }
        }

        Set<String> objectNames = pageSpec.getObjects().keySet();
        PageValidation pageValidation = new PageValidation(browser, browser.getPage(), pageSpec, null, null);

        PageDump pageDump = new PageDump();
        pageDump.setTitle(browser.getPage().getTitle());

        List<Pattern> patterns = convertPatterns(excludedObjects);

        Set<String> finalObjectNames = new HashSet<>();
        finalObjectNames.addAll(objectNames);
        finalObjectNames.add("screen");
        finalObjectNames.add("viewport");

        for (String objectName : finalObjectNames) {
            if (!matchesExcludedPatterns(objectName, patterns)) {
                PageElement pageElement = pageValidation.findPageElement(objectName);

                if (pageElement.isVisible() && pageElement.getArea() != null) {
                    PageDump.Element element = new PageDump.Element(objectName, pageElement.getArea().toIntArray());

                    if (pageElement.isPresent() && pageElement.isVisible() && isWithinArea(pageElement, maxWidth, maxHeight)) {
                        element.setHasImage(true);
                    }
                    pageDump.addElement(element);
                }
            }
        }

        if (!onlyImages) {
            pageDump.setPageName(pageName);
            exportAsJson(pageDump, new File(reportFolder.getAbsoluteFile() + File.separator + "page.json"));
            exportAsHtml(pageDump, pageName, new File(reportFolder.getAbsoluteFile() + File.separator + "page.html"));
            copyResource("/html-report/jquery-1.11.2.min.js", new File(reportFolder.getAbsolutePath() + File.separator + "jquery-1.11.2.min.js"));
            copyResource("/pagedump/galen-pagedump.js", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.js"));
            copyResource("/pagedump/galen-pagedump.css", new File(reportFolder.getAbsolutePath() + File.separator + "galen-pagedump.css"));
        }

        exportAllScreenshots(pageDump, browser, reportFolder);
    }

    private boolean matchesExcludedPatterns(String objectName, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(objectName).matches()) {
                return true;
            }
        }
        return false;
    }

    private List<Pattern> convertPatterns(List<String> excludedObjects) {
        List<Pattern> patterns = new LinkedList<>();
        if (excludedObjects != null) {
            for (String excludedObject : excludedObjects) {
                patterns.add(GalenUtils.convertObjectNameRegex(excludedObject));
            }
        }
        return patterns;
    }

    public void exportAsJson(PageDump pageDump, File file) throws IOException {
        makeSureFileExists(file);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, pageDump);
    }

    public void exportAsHtml(PageDump pageDump, String title, File file) throws IOException {
        makeSureFileExists(file);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonText = objectMapper.writeValueAsString(pageDump);

        String template = IOUtils.toString(getClass().getResourceAsStream("/pagedump/page.html"));

        String htmlText = template.replace("${title}", title);
        htmlText = htmlText.replace("${json}", jsonText);


        FileUtils.writeStringToFile(file, htmlText);
    }

    public void makeSureFileExists(File file) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Couldn't create file: " + file.getAbsolutePath());
            }
        }
    }

    public void exportAllScreenshots(PageDump pageDump, Browser browser, File reportFolder) throws IOException {

        File screenshotOriginalFile = browser.createScreenshot();

        FileUtils.copyFile(screenshotOriginalFile, new File(reportFolder.getAbsolutePath() + File.separator + "page.png"));

        BufferedImage image = Rainbow4J.loadImage(screenshotOriginalFile.getAbsolutePath());


        File objectsFolder = new File(reportFolder.getAbsolutePath() + File.separator + "objects");
        objectsFolder.mkdirs();

        for (PageDump.Element element : pageDump.getItems().values()) {
            if (element.getHasImage()) {
                int[] area = element.getArea();
                int availableHeight = image.getHeight() - area[1];
                int availableWidth = image.getWidth() - area[0];
                int subimageWidth = Math.min(area[2], availableWidth);
                int subimageHeight = Math.min(area[3], availableHeight);

                try {
                    BufferedImage subImage = image.getSubimage(area[0], area[1], subimageWidth, subimageHeight);
                    Rainbow4J.saveImage(subImage, new File(objectsFolder.getAbsolutePath() + File.separator + element.getObjectName() + ".png"));
                }
                catch (Exception ex) {
                    LOG.error("Got error during saving image", ex);
                }
            }
        }
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

    public String getPageName() {
        return pageName;
    }

    public GalenPageDump setPageName(String pageName) {
        this.pageName = pageName;
        return this;
    }

    public GalenPageDump setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public GalenPageDump setJsVariables(Map<String, Object> jsVariables) {
        this.jsVariables = jsVariables;
        return this;
    }

    public void setObjects(Map<String, Locator> objects) {
        this.objects = objects;
    }


    public Integer getMaxHeight() {
        return maxHeight;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public GalenPageDump setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public GalenPageDump setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public GalenPageDump setOnlyImages(boolean onlyImages) {
        this.onlyImages = onlyImages;
        return this;
    }

    public boolean isOnlyImages() {
        return onlyImages;
    }

    public GalenPageDump setExcludedObjects(List<String> excludedObjects) {
        this.excludedObjects = excludedObjects;
        return this;
    }

    public List<String> getExcludedObjects() {
        return excludedObjects;
    }
}
