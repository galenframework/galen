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
package com.galenframework.reports;

import com.galenframework.reports.json.JsonReportBuilder;
import com.galenframework.reports.json.ReportOverview;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

import static com.galenframework.utils.GalenUtils.makeSureFolderExists;

public class HtmlReportBuilder {
    private static final String[] resources = new String[]{
            "galen-report.js",
            "handlebars-v2.0.0.js",
            "icon-sprites.png",
            "jquery-1.11.2.min.js",
            "report.css",
            "tablesorter.css",
            "tablesorter.js"
    };


    public void build(List<GalenTestInfo> tests, String reportFolderPath) throws IOException {
        makeSureFolderExists(reportFolderPath);

        JsonReportBuilder jsonBuilder = new JsonReportBuilder();
        ReportOverview reportOverview = jsonBuilder.createReportOverview(tests);

        String overviewTemplate = IOUtils.toString(getClass().getResourceAsStream("/html-report/report.tpl.html"));
        String testReportTemplate = IOUtils.toString(getClass().getResourceAsStream("/html-report/report-test.tpl.html"));

        for (GalenTestAggregatedInfo aggregatedInfo : reportOverview.getTests()) {
            String testReportJson = jsonBuilder.exportTestReportToJsonString(aggregatedInfo);
            FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + aggregatedInfo.getTestId() + ".html"),
                    testReportTemplate
                            .replace("##REPORT-TEST-NAME##", aggregatedInfo.getTestInfo().getName())
                            .replace("##REPORT-DATA##", testReportJson));

            FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + aggregatedInfo.getTestId() + ".json"),
                    testReportJson);

            aggregatedInfo.getTestInfo().getReport().getFileStorage().copyAllFilesTo(new File(reportFolderPath));
        }

        String overviewJson = jsonBuilder.exportReportOverviewToJsonAsString(reportOverview);

        FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + "report.html"),
                overviewTemplate.replace("##REPORT-DATA##", overviewJson));

        FileUtils.writeStringToFile(new File(reportFolderPath + File.separator + "report.json"), overviewJson);

        copyHtmlResources(reportFolderPath);
    }

    private void copyHtmlResources(String reportFolderPath) throws IOException {

        for (String resourceName : resources) {
            copyResourceToFolder("/html-report/" + resourceName, reportFolderPath + File.separator + resourceName);
        }
    }

    private void copyResourceToFolder(String resourcePath, String destFileName) throws IOException {
        File destFile = new File(destFileName);

        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                throw new RuntimeException("Cannot copy file to: " + destFile.getAbsolutePath());
            }
        }
        IOUtils.copy(getClass().getResourceAsStream(resourcePath), new FileOutputStream(destFile));
    }

}
