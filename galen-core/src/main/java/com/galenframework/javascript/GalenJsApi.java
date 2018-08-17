/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.javascript;

import com.galenframework.api.UnregisteredTestSession;
import com.galenframework.page.Page;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.tests.TestSession;
import com.galenframework.utils.GalenUtils;
import com.galenframework.api.Galen;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.reports.TestReport;
import com.galenframework.reports.model.LayoutReport;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;

/**
 * This class is used for JavaScript functions defined in GalenApi.js
 */
public class GalenJsApi {

    private static final boolean APPEND = true;

    public static class JsVariable {
        private final String name;
        private final Object value;
        public JsVariable(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class JsPageObject {
        private final String name;
        private final String locator;

        public JsPageObject(String name, String locator) {
            this.name = name;
            this.locator = locator;
        }
    }

    /**
     * Needed for Javascript based tests
     * @throws IOException
     */
    public static LayoutReport checkLayout(WebDriver driver, String fileName, String[]includedTags, String[]excludedTags,
                                           String sectionNameFilter, Properties properties, String screenshotFilePath,
                                           JsVariable[] vars, JsPageObject[] jsPageObjects) throws IOException {

        TestSession session = TestSession.current();
        if (session == null) {
            throw new UnregisteredTestSession("Cannot check layout as there was no TestSession created");
        }

        TestReport report = session.getReport();

        File screenshotFile = null;

        if (screenshotFilePath != null) {
            screenshotFile = new File(screenshotFilePath);
            if (!screenshotFile.exists() || !screenshotFile.isFile()) {
                throw new IOException("Couldn't find screenshot in " + screenshotFilePath);
            }
        }

        if (fileName == null) {
            throw new IOException("Spec file name is not defined");
        }

        List<String> includedTagsList = toList(includedTags);

        Map<String, Object> jsVariables = convertJsVariables(vars);

        LayoutReport layoutReport = Galen.checkLayout(new SeleniumBrowser(driver), fileName,
                new SectionFilter(includedTagsList, toList(excludedTags)).withSectionName(sectionNameFilter),
                properties,
                jsVariables,
                screenshotFile,
                session.getListener(), convertObjects(jsPageObjects));

        GalenUtils.attachLayoutReport(layoutReport, report, fileName, includedTagsList);
        return layoutReport;
    }

    /**
     * Used in GalenApi.js
     * @param driver
     * @param pageSpec
     * @param includedTags
     * @param excludedTags
     * @param screenshotFilePath
     * @return
     * @throws IOException
     */
    public static LayoutReport checkPageSpecLayout(WebDriver driver, PageSpec pageSpec, String[]includedTags, String[]excludedTags,
                                           String screenshotFilePath) throws IOException {
        TestSession session = TestSession.current();
        if (session == null) {
            throw new UnregisteredTestSession("Cannot check layout as there was no TestSession created");
        }

        TestReport report = session.getReport();

        File screenshotFile = null;

        if (screenshotFilePath != null) {
            screenshotFile = new File(screenshotFilePath);
            if (!screenshotFile.exists() || !screenshotFile.isFile()) {
                throw new IOException("Couldn't find screenshot in " + screenshotFilePath);
            }
        }

        if (pageSpec == null) {
            throw new IOException("Page spec is not defined");
        }

        List<String> includedTagsList = toList(includedTags);

        LayoutReport layoutReport = Galen.checkLayout(new SeleniumBrowser(driver), pageSpec,
                new SectionFilter(includedTagsList, toList(excludedTags)),
                screenshotFile,
                session.getListener());

        GalenUtils.attachLayoutReport(layoutReport, report, "<unknown>", includedTagsList);
        return layoutReport;
    }


    private static Map<String, Locator> convertObjects(JsPageObject[] jsPageObjects) {
        Map<String, Locator> objects = new HashMap<>();

        if (jsPageObjects != null) {
            for (JsPageObject jsPageObject : jsPageObjects) {
                objects.put(jsPageObject.name, fromGalenPagesLocator(jsPageObject.locator));
            }
        }

        return objects;
    }

    private static Locator fromGalenPagesLocator(String locatorText) {
        if (locatorText == null) {
            throw new IllegalArgumentException("Locator cannot be null");
        }

        locatorText = locatorText.trim();

        int index = locatorText.indexOf(":");
        if (index > 0) {
            String type = locatorText.substring(0, index);
            String value = locatorText.substring(index + 1);
            return new Locator(type, value.trim());
        } else {
            return new Locator("css", locatorText);
        }
    }

    private static Map<String, Object> convertJsVariables(JsVariable[] vars) {
        Map<String, Object> converted = new HashMap<>();

        if (vars != null) {
            for (JsVariable variable : vars) {
                converted.put(variable.name, variable.value);
            }
        }
        return converted;
    }

    public static void resizeDriver(WebDriver driver, String sizeText) {
        GalenUtils.resizeDriver(driver, sizeText);
    }

    public static PageSpec parsePageSpec(WebDriver driver, String specPath, String[]includedTags, String[]excludedTags,
                Properties properties, JsVariable[] vars, JsPageObject[] jsPageObjects) throws IOException {
        PageSpecReader reader = new PageSpecReader();
        Page page = new SeleniumBrowser(driver).getPage();

        SectionFilter sectionFilter = new SectionFilter(toList(includedTags), toList(excludedTags));
        Map<String, Object> jsVariables = convertJsVariables(vars);

        return reader.read(specPath, page, sectionFilter, properties, jsVariables, convertObjects(jsPageObjects));
    }


    public static String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }

    public static boolean makeDirectory(String dirPath) {
        return new File(dirPath).mkdirs();
    }

    public static boolean isDirectory(String dirPath) {
        return new File(dirPath).isDirectory();
    }

    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    public static String[] listDirectory(String dirPath) throws IOException {
        File file = new File(dirPath);

        if (!file.exists()) {
            throw new FileNotFoundException(dirPath);
        } else if (!file.isDirectory()) {
            throw new IOException("Not a directory: " + dirPath);
        }

        return file.list();
    }

    public static boolean createFile(String filePath) throws IOException {
        return new File(filePath).createNewFile();
    }

    public static boolean deleteFile(String filePath) throws IOException {
        return new File(filePath).delete();
    }

    public static void writeFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Couldn't create file: " + filePath);
            }
        }
        FileUtils.writeStringToFile(file, text);
    }

    public static void appendFile(String filePath, String text) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), text, APPEND);
    }


    public static List<String> toList(String[] array) {
        if (array != null) {
            return asList(array);
        }
        return null;
    }
}
