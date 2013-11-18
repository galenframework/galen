/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;
import net.mindengine.galen.components.report.ReportingListenerTestUtils;
import net.mindengine.galen.reports.ConsoleReportingListener;
import net.mindengine.galen.reports.HtmlReportingListener;
import net.mindengine.galen.reports.TestngReportingListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class ReportingListenerTest {
    
    @Test public void shouldReport_inTestNgFormat_successfully() throws IOException {
        String reportPath = Files.createTempDir().getAbsolutePath() + "/report.xml";
        
        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "T00:00:00Z";
        
        TestngReportingListener listener = new TestngReportingListener(reportPath);
        ReportingListenerTestUtils.performSampleReporting("Home page on mobile", listener, listener);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
        
        listener.done();
        
        String realXml = FileUtils.readFileToString(new File(reportPath));
        
        Assert.assertEquals(expectedXml.replace("{expected-date}", expectedDate).replace("\\t    ", "\t"),
                realXml
                    .replaceAll("T([0-9]{2}:){2}[0-9]{2}Z", "T00:00:00Z")
                    .replaceAll("duration-ms=\"[0-9]+\"", "duration-ms=\"0\""));
    }
    
    @Test public void shouldReport_inHtmlFormat_successfully_andSplitFiles_perTest() throws IOException {
        String reportDirPath = Files.createTempDir().getAbsolutePath() + "/reports";
        
        HtmlReportingListener listener = new HtmlReportingListener(reportDirPath);
        ReportingListenerTestUtils.performSampleReporting("Some page test 1", listener, listener);
        ReportingListenerTestUtils.performSampleReporting("Some page test 2", listener, listener);
        
        listener.done();
        
        String expectedGeneralHtml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/report.html"));
        String realGeneralHtml = FileUtils.readFileToString(new File(reportDirPath + "/report.html"));
        Assert.assertEquals(expectedGeneralHtml, realGeneralHtml);
        
        String expectedSuite1Html = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/suite-1.html"));
        String realSuite1Html = FileUtils.readFileToString(new File(reportDirPath + "/report-1-some-page-test-1.html"));
        
        Assert.assertEquals(expectedSuite1Html, realSuite1Html);
        
        String expectedSuite2Html = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/suite-2.html"));
        String realSuite2Html = FileUtils.readFileToString(new File(reportDirPath + "/report-2-some-page-test-2.html"));
        Assert.assertEquals(expectedSuite2Html, realSuite2Html);
        
        assertThat("Should place screenshot 1 in same folder", new File(reportDirPath + "/report-1-some-page-test-1-screenshot-1.png").exists(), is(true));
        assertThat("Should place screenshot 2 in same folder", new File(reportDirPath + "/report-1-some-page-test-1-screenshot-2.png").exists(), is(true));
        assertThat("Should place screenshot 1 in same folder", new File(reportDirPath + "/report-2-some-page-test-2-screenshot-1.png").exists(), is(true));
        assertThat("Should place screenshot 2 in same folder", new File(reportDirPath + "/report-2-some-page-test-2-screenshot-2.png").exists(), is(true));
        
        assertThat("Should place css same folder", new File(reportDirPath + "/galen-report.css").exists(), is(true));
        assertThat("Should place js same folder", new File(reportDirPath + "/galen-report.js").exists(), is(true));
        assertThat("Should place jquery same folder", new File(reportDirPath + "/jquery-1.10.2.min.js").exists(), is(true));
    }

    @Test public void shouldReport_toConsole_successfully() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ConsoleReportingListener listener = new ConsoleReportingListener(ps, ps);
        ReportingListenerTestUtils.performSampleReporting("page1.test", listener, listener);
        
        listener.done();
        
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/console.txt")).replace("\\t    ", "\t");
        
        Assert.assertEquals(expectedText, baos.toString("UTF-8"));
    }
    
}
