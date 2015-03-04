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
package net.mindengine.galen.reports;

import net.mindengine.galen.reports.json.JsonReportBuilder;
import net.mindengine.galen.reports.json.ReportOverview;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

public class HtmlReportBuilder {
    //@formatter:off
    private static final String[] resources = new String[]{
        "galen-report.js",
        "handlebars-v2.0.0.js",
        "icon-sprites.png",
        "jquery-1.11.2.min.js",
        "report.css",
        "tablesorter.css",
        "tablesorter.js"
    };
    //@formatter:on

    public void build(final List<GalenTestInfo> tests, final String reportFolderPath) throws IOException {
        makeSureReportFolderExists(reportFolderPath);

        final JsonReportBuilder jsonBuilder = new JsonReportBuilder();
        final ReportOverview reportOverview = jsonBuilder.createReportOverview(tests);

        final String overviewTemplate = IOUtils.toString(getClass().getResourceAsStream("/html-report/report.tpl.html"));
        final String testReportTemplate = IOUtils.toString(getClass().getResourceAsStream("/html-report/report-test.tpl.html"));

        for (final GalenTestAggregatedInfo aggregatedInfo : reportOverview.getTests()) {
            final String testReportJson = jsonBuilder.exportTestReportToJsonString(aggregatedInfo);
            FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + aggregatedInfo.getTestId() + ".html"),
                    testReportTemplate.replace("##REPORT-TEST-NAME##", aggregatedInfo.getTestInfo().getName()).replace("##REPORT-DATA##", testReportJson));

            aggregatedInfo.getTestInfo().getReport().getFileStorage().copyAllFilesTo(new File(reportFolderPath));
        }

        final String overviewJson = jsonBuilder.exportReportOverviewToJsonAsString(reportOverview);

        FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + "report.html"), overviewTemplate.replace("##REPORT-DATA##", overviewJson));

        copyHtmlResources(reportFolderPath);
    }

    private void makeSureReportFolderExists(final String reportFolderPath) throws IOException {
        FileUtils.forceMkdir(new File(reportFolderPath));
    }

    private void copyHtmlResources(final String reportFolderPath) throws IOException {

        for (final String resourceName : resources) {
            copyResourceToFolder("/html-report/" + resourceName, reportFolderPath + File.separator + resourceName);
        }
    }

    private void copyResourceToFolder(final String resourcePath, final String destFileName) throws IOException {
        final File destFile = new File(destFileName);

        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                throw new RuntimeException("Cannot copy file to: " + destFile.getAbsolutePath());
            }
        }
        IOUtils.copy(getClass().getResourceAsStream(resourcePath), new FileOutputStream(destFile));
    }

}
