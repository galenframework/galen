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
package net.mindengine.galen.javascript;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.api.UnregisteredTestSession;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.nodes.LayoutReportNode;
import net.mindengine.galen.reports.nodes.TestReportNode;
import net.mindengine.galen.tests.TestSession;
import net.mindengine.galen.utils.GalenUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;

/**
 * This class is used for JavaScript functions defined in GalenApi.js
 */
public class GalenJsApi {
    /**
     * Needed for Javascript based tests
     * @param driver
     * @param fileName
     * @param includedTags
     * @param excludedTags
     * @param screenshotFilePath
     * @throws IOException
     */
    public static void checkLayout(WebDriver driver, String fileName, String[]includedTags, String[]excludedTags, Properties properties, String screenshotFilePath, Map<String, Object> jsVariables) throws IOException {

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

        LayoutReport layoutReport = Galen.checkLayout(new SeleniumBrowser(driver), fileName,
                includedTagsList, toList(excludedTags),
                properties,
                jsVariables,
                screenshotFile,
                session.getListener());

        if (report != null) {
            String reportTitle = "Check layout: " + fileName + " included tags: " + GalenUtils.toCommaSeparated(includedTagsList);
            TestReportNode layoutReportNode = new LayoutReportNode(report.getFileStorage(), layoutReport, reportTitle);
            if (layoutReport.errors() > 0) {
                layoutReportNode.setStatus(TestReportNode.Status.ERROR);
            }
            report.addNode(layoutReportNode);
        }
    }

    public static void resizeDriver(WebDriver driver, String sizeText) {
        GalenUtils.resizeDriver(driver, sizeText);
    }


    private static List<String> toList(String[] array) {
        if (array != null) {
            return asList(array);
        }
        return null;
    }
}
