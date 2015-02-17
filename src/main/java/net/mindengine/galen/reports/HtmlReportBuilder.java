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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;
import net.mindengine.galen.reports.nodes.LayoutReportNode;
import net.mindengine.galen.reports.nodes.TestReportNode;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.rainbow4j.Rainbow4J;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlReportBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(HtmlReportBuilder.class);

    private final TestIdGenerator testIdGenerator = new TestIdGenerator();
    private final UniqueIdGenerator comparisonMapUniqueIdGenerator = new UniqueIdGenerator();
    private Configuration freemarkerConfiguration = new Configuration();

    public void build(List<GalenTestInfo> tests, String reportFolderPath) throws IOException, TemplateException {
        List<GalenTestAggregatedInfo> aggregatedTests = new LinkedList<GalenTestAggregatedInfo>();

        for (GalenTestInfo test : tests) {
            GalenTestAggregatedInfo aggregatedInfo = new GalenTestAggregatedInfo("report-" + testIdGenerator.generateTestId(test.getName()), test);
            aggregatedTests.add(aggregatedInfo);

            try {
                exportTestReport(aggregatedInfo, reportFolderPath);
            } catch (Exception ex) {
                LOG.error("Unknown report export", ex);
            }
        }
        exportMainReport(reportFolderPath, aggregatedTests);
    }

    private void exportTestReport(GalenTestAggregatedInfo aggregatedInfo, String reportFolderPath) throws IOException, TemplateException {
        makeSureReportFolderExists(reportFolderPath);

        File file = createTestReportFile(aggregatedInfo, reportFolderPath);
        moveAllAttachmentsInReport(aggregatedInfo, reportFolderPath);

        FileWriter fileWriter = new FileWriter(file);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("test", aggregatedInfo);

        Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/html-report/report-test.ftl.html")),
                freemarkerConfiguration);
        template.process(model, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    private void moveAllAttachmentsInReport(GalenTestAggregatedInfo aggregatedInfo, String reportFolderPath) throws IOException {
        TestReport report = aggregatedInfo.getTestInfo().getReport();

        for (Map.Entry<String, File> entry: report.getFileStorage().getFiles().entrySet()) {
            FileUtils.copyFile(entry.getValue(), new File(reportFolderPath + File.separator + entry.getKey()));
        }
    }





    private File createTestReportFile(GalenTestAggregatedInfo aggregatedInfo, String reportFolderPath) throws IOException {
        File file = new File(reportFolderPath + File.separator + String.format("%s.html", aggregatedInfo.getTestId()));
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Cannot create file: " + file.getAbsolutePath());
            }
        }
        return file;
    }

    private void exportMainReport(String reportFolderPath, List<GalenTestAggregatedInfo> tests) throws IOException, TemplateException {
        makeSureReportFolderExists(reportFolderPath);

        File file = new File(reportFolderPath + File.separator + "report.html");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Cannot create file: " + file.getAbsolutePath());
            }
        }
        FileWriter fileWriter = new FileWriter(file);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("tests", tests);

        Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/html-report/report.ftl.html")),
                freemarkerConfiguration);
        template.process(model, fileWriter);
        fileWriter.flush();
        fileWriter.close();

        copyHtmlResources(reportFolderPath);
    }

    private void makeSureReportFolderExists(String reportFolderPath) throws IOException {
        File reportFolder = new File(reportFolderPath);
        if (!reportFolder.exists()) {
            if (!reportFolder.mkdirs()) {
                throw new IOException("Could not create directories: " + reportFolderPath);
            }
        }
    }

    private void copyHtmlResources(String reportFolderPath) throws IOException {
        // copy sorting libs
        copyResourceToFolder("/html-report/tablesorter.css", reportFolderPath + File.separator + "tablesorter.css");
        copyResourceToFolder("/html-report/tablesorter.js", reportFolderPath + File.separator + "tablesorter.js");
        // copy galen libs
        copyResourceToFolder("/html-report/galen-report.css", reportFolderPath + File.separator + "galen-report.css");
        copyResourceToFolder("/html-report/galen-report.js", reportFolderPath + File.separator + "galen-report.js");
        copyResourceToFolder("/html-report/jquery-1.10.2.min.js", reportFolderPath + File.separator + "jquery-1.10.2.min.js");
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
