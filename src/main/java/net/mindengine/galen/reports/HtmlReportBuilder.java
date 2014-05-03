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

import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlReportBuilder {
    private Configuration freemarkerConfiguration = new Configuration();
    
    public void build(List<GalenTestInfo> tests, String reportFolderPath) throws IOException, TemplateException {
        Template testTemplate = new Template("suite-report", new InputStreamReader(getClass().getResourceAsStream("/html-report/report-suite.ftl.html")), freemarkerConfiguration);
        
        
        List<GalenTestAggregatedInfo> aggregatedTests = new LinkedList<GalenTestAggregatedInfo>();
        
        for (GalenTestInfo test : tests) {
            aggregatedTests.add(new GalenTestAggregatedInfo(test));
        }
        exportMainReport(reportFolderPath, aggregatedTests);
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
