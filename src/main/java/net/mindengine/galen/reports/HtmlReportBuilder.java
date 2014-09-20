/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.rainbow4j.Rainbow4J;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlReportBuilder {
    
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
            }
            catch (Exception ex) {
                ex.printStackTrace();
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
        
        Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/html-report/report-test.ftl.html")), freemarkerConfiguration);
        template.process(model, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    private void moveAllAttachmentsInReport(GalenTestAggregatedInfo aggregatedInfo, String reportFolderPath) {
        TestReport report = aggregatedInfo.getTestInfo().getReport();
        if (report.getNodes() != null) {
            for (TestReportNode node: report.getNodes()) {
                moveAttachmentsInReportNode(node, reportFolderPath, aggregatedInfo.getTestId());
            }
        }
    }

    
    private void moveAttachmentsInReportNode(TestReportNode node, String reportFolderPath, String filePrefix) {
        
        if (node.getAttachments() != null) {
            for (TestAttachment attachment : node.getAttachments()) {
                moveAttachmentFile(attachment, reportFolderPath, filePrefix);
            }
        }
        
        if (node instanceof LayoutReportNode) {
            moveAllFilesForLayoutReport((LayoutReportNode) node, reportFolderPath, filePrefix);
        }
        
        if (node.getNodes() != null) {
            for (TestReportNode subNode : node.getNodes()) {
                moveAttachmentsInReportNode(subNode, reportFolderPath, filePrefix);
            }
        }
    }

    private void moveAttachmentFile(TestAttachment attachment, String reportFolderPath, String filePrefix) {
        if (attachment.getFile() != null) {
            String fileName = createUniqueFileName(filePrefix + "-attachment", "-" + attachment.getFile().getName());
            try {
                FileUtils.copyFile(attachment.getFile(), new File(reportFolderPath + File.separator + fileName));
                attachment.setPathInReport(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveAllFilesForLayoutReport(LayoutReportNode node, String reportFolderPath, String filePrefix) {
        if (node.getLayoutReport() != null && node.getLayoutReport().getScreenshotFullPath() != null) {
            String fileName = createUniqueFileName(filePrefix + "-screenshot", ".png");
            
            node.getLayoutReport().setScreenshot(fileName);
            try {
                FileUtils.copyFile(new File(node.getLayoutReport().getScreenshotFullPath()), new File(reportFolderPath + File.separator + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            searchForLayoutAttachmentsAndMoveThem(node.getLayoutReport(), reportFolderPath, filePrefix);
        }
    }

    private void searchForLayoutAttachmentsAndMoveThem(LayoutReport layoutReport, String reportFolderPath, String filePrefix) {
        for (LayoutSection section : layoutReport.getSections()) {
            for (LayoutObject layoutObject: section.getObjects()) {
                searchForLayoutAttachmentsAndMoveThem(layoutObject, reportFolderPath, filePrefix);
            }
        }
    }

    private void searchForLayoutAttachmentsAndMoveThem(LayoutObject layoutObject, String reportFolderPath, String filePrefix) {

        for (LayoutSpec spec : layoutObject.getSpecs()) {
            if (spec.getImageComparison() != null) {

                File reportFolder = new File(reportFolderPath + File.separator + filePrefix);
                reportFolder.mkdirs();

                String newName = moveFileOrResourceTo(spec.getImageComparison().getImageSamplePath(), reportFolder);

                spec.getImageComparison().setImageSamplePath(filePrefix + "/" + newName);

                if (spec.getImageComparison().getComparisonMap() != null) {
                    try {
                        String comparisonMapPath = layoutObject.getName() + "-" + comparisonMapUniqueIdGenerator.uniqueId() + ".map.png";
                        File mapFile = new File(reportFolder.getAbsoluteFile() + File.separator + comparisonMapPath);
                        spec.getImageComparison().setComparisonMapPath(filePrefix + "/" + comparisonMapPath);
                        Rainbow4J.saveImage(spec.getImageComparison().getComparisonMap(), mapFile);
                    }
                    catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (layoutObject.getSubObjects() != null) {
            for (LayoutObject subObject : layoutObject.getSubObjects()) {
                searchForLayoutAttachmentsAndMoveThem(subObject, reportFolderPath, filePrefix);
            }
        }
    }

    private String moveFileOrResourceTo(String imagePath, File reportFolder) {
        try {
            InputStream stream = GalenUtils.findFileOrResourceAsStream(imagePath);

            if (stream == null) {
                throw new FileNotFoundException(imagePath);
            }

            String newName = GalenUtils.convertToFileName(imagePath);

            File file = new File(reportFolder.getAbsolutePath() + File.separator + newName);
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copy(stream, fos);
            fos.flush();
            fos.close();
            return newName;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return imagePath;
        }
    }

    private Long _uniqueId = 0L;
    private synchronized String createUniqueFileName(String prefix, String suffix) {
        _uniqueId++;
        return String.format("%s-%d%s", prefix, _uniqueId, suffix);
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
        
        Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/html-report/report.ftl.html")), freemarkerConfiguration);
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
