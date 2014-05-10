package net.mindengine.galen.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlReportBuilder {
    
    private TestIdGenerator testIdGenerator = new TestIdGenerator();
    private Configuration freemarkerConfiguration = new Configuration();
    
    public void build(List<GalenTestInfo> tests, String reportFolderPath) throws IOException, TemplateException {
        List<GalenTestAggregatedInfo> aggregatedTests = new LinkedList<GalenTestAggregatedInfo>();
        
        for (GalenTestInfo test : tests) {
            GalenTestAggregatedInfo aggregatedInfo = new GalenTestAggregatedInfo(testIdGenerator.generateTestId(test.getName()), test);
            aggregatedTests.add(aggregatedInfo);
            
            exportTestReport(aggregatedInfo, reportFolderPath);
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
        for (TestReportNode node: report.getNodes()) {
            moveAttachmentsInReportNode(node, reportFolderPath, aggregatedInfo.getTestId());
        }
    }

    
    private void moveAttachmentsInReportNode(TestReportNode node, String reportFolderPath, String filePrefix) {
        
        if (node.getAttachments() != null) {
            for (TestAttachment attachment : node.getAttachments()) {
                moveAttachmentFile(attachment, reportFolderPath, filePrefix);
            }
        }
        
        if (node instanceof LayoutReportNode) {
            moveScreenshotFile((LayoutReportNode)node, reportFolderPath, filePrefix);
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

    private void moveScreenshotFile(LayoutReportNode node, String reportFolderPath, String filePrefix) {
        if (node.getLayoutReport() != null && node.getLayoutReport().getScreenshotFullPath() != null) {
            String fileName = createUniqueFileName(filePrefix + "-screenshot", ".png");
            
            node.getLayoutReport().setScreenshot(fileName);
            try {
                FileUtils.copyFile(new File(node.getLayoutReport().getScreenshotFullPath()), new File(reportFolderPath + File.separator + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
