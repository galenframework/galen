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
package net.mindengine.galen.tests.runner;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import net.mindengine.galen.components.report.FakeException;
import net.mindengine.galen.components.report.ReportingListenerTestUtils;
import net.mindengine.galen.reports.*;
import net.mindengine.galen.reports.json.JsonReportBuilder;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.nodes.LayoutReportNode;
import net.mindengine.galen.reports.LayoutReportListener;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

import freemarker.template.TemplateException;

public class ReportingTest {
    
    private static final String GALEN_LOG_LEVEL = "galen.log.level";

    @AfterMethod public void removeAllSystemProperties() {
        System.getProperties().remove(GALEN_LOG_LEVEL);
    }
    
    


    @Test
    public void shouldReport_inJsonFormat() throws IOException {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/json-report";
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);
        TestReport report = new TestReport();
        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setScreenshotFullPath(File.createTempFile("screenshot", ".png").getAbsolutePath());
        ReportingListenerTestUtils.performSampleReporting("Home page test", null, new LayoutReportListener(layoutReport), null);

        report.addNode(new LayoutReportNode(layoutReport, "check layout"));
        report.getNodes().get(0).setTime(new Date(1404681346000L));


        testInfo.setReport(report);
        testInfos.add(testInfo);
        testInfo.setStartedAt(new Date(1404681346000L));
        testInfo.setEndedAt(new Date(1404681416000L));


        new JsonReportBuilder().build(testInfos, reportPath);

        assertJsonFileContents("Report overview", reportPath + "/report.json", "/expected-reports/json/report.json");
        assertJsonFileContents("Test report", reportPath + "/1-home-page-test.json", "/expected-reports/json/1-home-page-test.json");
    }


    @Test public void shouldReport_inTestNgFormat_successfully() throws IOException, TemplateException {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/testng-report/report.xml";
        
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);

        testInfo.setReport(new TestReport());
        testInfo.setStartedAt(new Date(1399741000000L));
        testInfo.setEndedAt(new Date(1399746930000L));
        testInfo.setException(new FakeException("Some exception here"));
        testInfos.add(testInfo);
        
        testInfo = new GalenTestInfo("Login page test", null);
        testInfo.setReport(new TestReport());
        testInfo.setStartedAt(new Date(1399741000000L));
        testInfo.setEndedAt(new Date(1399746930000L));
        testInfos.add(testInfo);
        
        
        new TestNgReportBuilder().build(testInfos, reportPath);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
       
        String realXml = readFileToString(new File(reportPath));
        
        Assert.assertEquals(trimEveryLine(expectedXml), trimEveryLine(realXml));
    }
    
    @Test public void shouldReport_inHtmlFormat_withException_andAttachments() throws IOException, TemplateException {
        String reportDirPath = Files.createTempDir().getAbsolutePath() + "/reports";
        
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);
        testInfo.setStartedAt(new Date(1399741000000L));
        testInfo.setEndedAt(new Date(1399746930000L));

        File attachmentFile = new File(Files.createTempDir().getAbsolutePath() + File.separator + "custom.txt");
        attachmentFile.createNewFile();
        
        testInfo.getReport().error(new FakeException("Some exception here")).withAttachment("custom.txt", attachmentFile);
        testInfo.getReport().info("Some detailed report").withDetails("Some details");
        testInfo.getReport().getNodes().get(0).setTime(new Date(1399741000000L));
        testInfo.getReport().getNodes().get(1).setTime(new Date(1399741000000L));
        testInfos.add(testInfo);
        new HtmlReportBuilder().build(testInfos, reportDirPath);
        
        String expectedSuite1Html = trimEveryLine(IOUtils.toString(getClass().getResourceAsStream("/expected-reports/test-with-attachment.html")));
        String realSuite1Html = trimEveryLine(readFileToString(new File(reportDirPath + "/report-1-home-page-test.html")));
        Assert.assertEquals(expectedSuite1Html, realSuite1Html);
        
        assertThat("Should place attachment file in same folder", new File(reportDirPath + "/report-1-home-page-test-attachment-1-custom.txt").exists(), is(true));
    }
    
    @Test public void shouldReport_inHtmlFormat_successfully_andSplitFiles_perTest() throws IOException, TemplateException {
        String reportDirPath = Files.createTempDir().getAbsolutePath() + "/reports";
        
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);

        TestReport report = new TestReport();
        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setScreenshotFullPath(File.createTempFile("screenshot", ".png").getAbsolutePath());
        ReportingListenerTestUtils.performSampleReporting("Home page test", null, new LayoutReportListener(layoutReport), null);



        report.addNode(new LayoutReportNode(layoutReport, "check layout"));
        report.getNodes().get(0).setTime(new Date(1404681346000L));


        testInfo.setReport(report);
        testInfos.add(testInfo);
        testInfo.setStartedAt(new Date(1404681346000L));
        testInfo.setEndedAt(new Date(1404681416000L));

        new HtmlReportBuilder().build(testInfos, reportDirPath);
        
        
        String expectedGeneralHtml = trimEveryLine(IOUtils.toString(getClass().getResourceAsStream("/expected-reports/report.html")));
        String realGeneralHtml = trimEveryLine(readFileToString(new File(reportDirPath + "/report.html")));
        Assert.assertEquals(expectedGeneralHtml, realGeneralHtml);
        
        String expectedSuite1Html = trimEveryLine(IOUtils.toString(getClass().getResourceAsStream("/expected-reports/test-1.html")));
        String realSuite1Html = trimEveryLine(readFileToString(new File(reportDirPath + "/report-1-home-page-test.html")));
        Assert.assertEquals(expectedSuite1Html, realSuite1Html);
        
        assertThat("Should place screenshot 1 in same folder", new File(reportDirPath + "/report-1-home-page-test-screenshot-1.png").exists(), is(true));

        assertThat("Should place image comparison image as an attachment to the report",
                new File(reportDirPath + "/report-1-home-page-test/imgs-button-sample-correct.png").exists(), is(true));

        assertThat("Should place image comparison map image as an attachment to the report",
                new File(reportDirPath + "/report-1-home-page-test/objectB1-1.map.png").exists(), is(true));
        
        assertThat("Should place css same folder", new File(reportDirPath + "/galen-report.css").exists(), is(true));
        assertThat("Should place js same folder", new File(reportDirPath + "/galen-report.js").exists(), is(true));
        assertThat("Should place jquery same folder", new File(reportDirPath + "/jquery-1.10.2.min.js").exists(), is(true));
    }

    private String trimEveryLine(String text) {
        String lines[] = text.split("\\r?\\n");
        StringBuilder builder = new StringBuilder();
        
        for (String line: lines) {
            builder.append(line.trim());
            builder.append("\n");
        }
        
        return builder.toString();
    }

    @Test public void shouldReport_toConsole_successfully() throws IOException {
        performConsoleReporting_andCompare("/expected-reports/console.txt");
    }

    @Test public void shouldReport_toConsole_onlySuites_whenLogLevel_is_1() throws IOException {
        System.setProperty(GALEN_LOG_LEVEL, "1");
        performConsoleReporting_andCompare("/expected-reports/console-1.txt");
    }
    
    @Test public void shouldReport_toConsole_onlySuites_andPages_whenLogLevel_is_2() throws IOException {
        System.setProperty(GALEN_LOG_LEVEL, "2");
        performConsoleReporting_andCompare("/expected-reports/console-2.txt");
    }
    
    private void performConsoleReporting_andCompare(String expectedReport) throws IOException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ConsoleReportingListener listener = new ConsoleReportingListener(ps, ps);
        ReportingListenerTestUtils.performSampleReporting("page1.test", listener, listener, listener);
        
        listener.done();
        String expectedText = IOUtils.toString(getClass().getResourceAsStream(expectedReport)).replace("\\t    ", "\t");
        
        Assert.assertEquals(expectedText, baos.toString("UTF-8"));
    }

    private void assertJsonFileContents(String title, String actualPath, String expectedPath) throws IOException {
        String actualContent = readFileToString(new File(actualPath));

        System.out.println("\n\n---- " + title + " -----------");
        System.out.println(actualContent);
        String expectedContent = readFileToString(new File(getClass().getResource(expectedPath).getFile()));


        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualTree = mapper.readTree(actualContent);
        JsonNode expectedTree = mapper.readTree(expectedContent);
        assertThat(title + " content should be", actualTree, is(expectedTree));
    }

}
