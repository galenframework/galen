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
package com.galenframework.javascript;

import com.galenframework.api.UnregisteredTestSession;
import com.galenframework.page.Page;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.reports.nodes.LayoutReportNode;
import com.galenframework.reports.nodes.TestReportNode;
import com.galenframework.speclang2.reader.pagespec.PageSpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.reader.page.PageSpec;
import com.galenframework.specs.reader.page.SectionFilter;
import com.galenframework.tests.TestSession;
import com.galenframework.utils.GalenUtils;
import com.galenframework.api.Galen;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.reports.TestReport;
import com.galenframework.reports.model.LayoutReport;
import org.openqa.selenium.WebDriver;

import java.io.File;
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
     * @param driver
     * @param fileName
     * @param includedTags
     * @param excludedTags
     * @param screenshotFilePath
     * @throws IOException
     */
    public static void checkLayout(WebDriver driver, String fileName, String[]includedTags, String[]excludedTags,
                                   Properties properties, String screenshotFilePath, JsVariable[] vars, JsPageObject[] jsPageObjects) throws IOException {

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
                new SectionFilter(includedTagsList, toList(excludedTags)),
                properties,
                jsVariables,
                screenshotFile,
                session.getListener(), convertObjects(jsPageObjects));

        if (report != null) {
            String reportTitle = "Check layout: " + fileName + " included tags: " + GalenUtils.toCommaSeparated(includedTagsList);
            TestReportNode layoutReportNode = new LayoutReportNode(report.getFileStorage(), layoutReport, reportTitle);
            if (layoutReport.errors() > 0) {
                layoutReportNode.setStatus(TestReportNode.Status.ERROR);
            }
            report.addNode(layoutReportNode);
        }
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


    public static List<String> toList(String[] array) {
        if (array != null) {
            return asList(array);
        }
        return null;
    }
}
