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
package com.galenframework.tests.runner;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.reports.*;
import junit.framework.Assert;
import com.galenframework.components.report.FakeException;
import com.galenframework.components.report.ReportingListenerTestUtils;
import com.galenframework.reports.json.JsonReportBuilder;
import com.galenframework.reports.model.FileTempStorage;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.reports.nodes.LayoutReportNode;

import com.galenframework.tests.GalenEmptyTest;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

import freemarker.template.TemplateException;

@Test(singleThreaded=true)
public class ReportingTest {
    
    private static final String GALEN_LOG_LEVEL = "galen.log.level";

    @AfterMethod public void removeAllSystemProperties() {
        System.getProperties().remove(GALEN_LOG_LEVEL);
    }
    

    @BeforeMethod
    public void resetUniqueId() throws NoSuchFieldException, IllegalAccessException {
        resetUniqueIdForFileTempStorage();
    }

    @Test
    public void shouldReportWithEmptyScreenshot_inJsonFormat() throws Exception {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/json-report";
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", new GalenEmptyTest("Home page test", asList("mobile", "HOMEPAGE")));
        TestReport report = new TestReport();
        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setScreenshot(null);
        ReportingListenerTestUtils.performSampleReporting("Home page test", null, new LayoutReportListener(layoutReport), null);

        report.info("Just a simple info node with attachment")
                .withAttachment("some-file.txt", File.createTempFile("some-file", ".txt"))
                .setTime(new Date(1404681346001L));

        report.addNode(new LayoutReportNode(report.getFileStorage(), layoutReport, "check layout"))
                .setTime(new Date(1404681346002L));


        testInfo.setReport(report);
        testInfos.add(testInfo);
        testInfo.setStartedAt(new Date(1404681346000L));
        testInfo.setEndedAt(new Date(1404681416000L));


        new JsonReportBuilder().build(testInfos, reportPath);

        assertJsonFileContents("Report overview", reportPath + "/report.json", "/expected-reports/json/report.json");
        assertJsonFileContents("Test report", reportPath + "/1-home-page-test.json", "/expected-reports/json/2-home-page-test.json");


        // Check that all files from storage were saved in report folder

        assertThat("Report folder contains files", asList(new File(reportPath).list()), containsInAnyOrder(
                "1-home-page-test.json",
                "file-4-some-file.txt",
                "layout-1-objectB1-actual.png",
                "layout-2-objectB1-expected.png",
                "layout-3-objectB1-map.png",
                "report.json"
        ));
    }

    @Test
    public void shouldReport_inJsonFormat() throws Exception {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/json-report";
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", new GalenEmptyTest("Home page test", asList("mobile", "HOMEPAGE")));
        TestReport report = new TestReport();
        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setScreenshot(layoutReport.getFileStorage().registerFile("screenshot.png", File.createTempFile("screenshot", ".png")));
        ReportingListenerTestUtils.performSampleReporting("Home page test", null, new LayoutReportListener(layoutReport), null);

        report.info("Just a simple info node with attachment")
                .withAttachment("some-file.txt", File.createTempFile("some-file", ".txt"))
                .setTime(new Date(1404681346001L));

        report.addNode(new LayoutReportNode(report.getFileStorage(), layoutReport, "check layout"))
                .setTime(new Date(1404681346002L));


        testInfo.setReport(report);
        testInfos.add(testInfo);
        testInfo.setStartedAt(new Date(1404681346000L));
        testInfo.setEndedAt(new Date(1404681416000L));


        new JsonReportBuilder().build(testInfos, reportPath);

        assertJsonFileContents("Report overview", reportPath + "/report.json", "/expected-reports/json/report.json");
        assertJsonFileContents("Test report", reportPath + "/1-home-page-test.json", "/expected-reports/json/1-home-page-test.json");


        // Check that all files from storage were saved in report folder

        assertThat("Report folder contains files", asList(new File(reportPath).list()), containsInAnyOrder(
                "1-home-page-test.json",
                "file-5-some-file.txt",
                "layout-1-screenshot.png",
                "layout-2-objectB1-actual.png",
                "layout-3-objectB1-expected.png",
                "layout-4-objectB1-map.png",
                "report.json"
        ));
    }

    private void resetUniqueIdForFileTempStorage() throws NoSuchFieldException, IllegalAccessException {
        Field _uniqueIdField = FileTempStorage.class.getDeclaredField("_uniqueId");
        _uniqueIdField.setAccessible(true);
        _uniqueIdField.set(null, 0L);
    }


    @Test public void shouldReport_inTestNgFormat_successfully() throws IOException, TemplateException {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/testng-report/report.xml";
        
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);

        testInfo.setReport(new TestReport());
        testInfo.setStartedAt(asDate(2014, 4, 10, 18, 56, 40));
        testInfo.setEndedAt(asDate(2014, 4, 10, 20, 35, 30));
        testInfo.setException(new FakeException("Some exception here"));
        testInfos.add(testInfo);
        
        testInfo = new GalenTestInfo("Login page test", null);
        testInfo.setReport(new TestReport());
        testInfo.setStartedAt(asDate(2014, 4, 10, 18, 56, 40));
        testInfo.setEndedAt(asDate(2014, 4, 10, 20, 35, 30));
        testInfos.add(testInfo);
        
        
        new TestNgReportBuilder().build(testInfos, reportPath);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
       
        String realXml = readFileToString(new File(reportPath));
        
        Assert.assertEquals(trimEveryLine(expectedXml), trimEveryLine(realXml));
    }

    private Date asDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second)
                .getTime();
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
        
        assertThat("Should place attachment file in same folder", new File(reportDirPath + "/file-1-custom.txt").exists(), is(true));
    }
    
    @Test public void shouldReport_inHtmlWithJsonFormat_successfully_andSplitFiles_perTest() throws IOException, TemplateException {
        String reportDirPath = Files.createTempDir().getAbsolutePath() + "/reports";
        
        List<GalenTestInfo> testInfos = new LinkedList<GalenTestInfo>();
        
        GalenTestInfo testInfo = new GalenTestInfo("Home page test", null);

        TestReport report = new TestReport();
        LayoutReport layoutReport = new LayoutReport();
        layoutReport.setScreenshot(layoutReport.getFileStorage().registerFile("screenshot.png", File.createTempFile("screenshot", ".png")));
        ReportingListenerTestUtils.performSampleReporting("Home page test", null, new LayoutReportListener(layoutReport), null);



        report.info("Just a simple info node with attachment")
                .withAttachment("some-file.txt", File.createTempFile("some-file", ".txt"))
                .setTime(new Date(1404681346001L));

        report.addNode(new LayoutReportNode(report.getFileStorage(), layoutReport, "check layout"))
                .setTime(new Date(1404681346002L));


        testInfo.setReport(report);
        testInfos.add(testInfo);
        testInfo.setStartedAt(new Date(1404681346000L));
        testInfo.setEndedAt(new Date(1404681416000L));

        new HtmlReportBuilder().build(testInfos, reportDirPath);
        
        assertThat("Report folder contains files", asList(new File(reportDirPath).list()), containsInAnyOrder(
                "1-home-page-test.html",
                "1-home-page-test.json",
                "file-5-some-file.txt",
                "layout-1-screenshot.png",
                "layout-2-objectB1-actual.png",
                "layout-3-objectB1-expected.png",
                "layout-4-objectB1-map.png",
                "report.html",
                "report.json",
                "handlebars-v2.0.0.js",
                "galen-report.js",
                "report.css",
                "icon-sprites.png",
                "jquery-1.11.2.min.js",
                "tablesorter.js",
                "tablesorter.css"
        ));
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
    
    private void performConsoleReporting_andCompare(String expectedReport) throws IOException {
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

        String expectedContent = readFileToString(new File(getClass().getResource(expectedPath).getFile()));

        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualTree = mapper.readTree(actualContent);
        JsonNode expectedTree = mapper.readTree(expectedContent);
        assertThat(title + " content should be", actualTree, is(expectedTree));
    }

}