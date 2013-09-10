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
import static org.hamcrest.Matchers.containsString;
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
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
        
        listener.done();
        
        String realXml = FileUtils.readFileToString(new File(reportPath));
        
        Assert.assertEquals(expectedXml.replace("{expected-date}", expectedDate).replace("\\t    ", "\t"),
                realXml.replaceAll("T([0-9]{2}:){2}[0-9]{2}Z", "T00:00:00Z"));
    }
    
    
    @Test public void shouldReport_inHtmlFormat_successfully() throws IOException {
        String reportDirPath = Files.createTempDir().getAbsolutePath();
        
        HtmlReportingListener listener = new HtmlReportingListener(reportDirPath + "/report.html");
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/html-report-suffix.html"));
        
        listener.done();
        
        String realHtml = FileUtils.readFileToString(new File(reportDirPath + "/report.html"));
        
        Assert.assertEquals(expectedHtml, bodyPart(realHtml));
        
        assertThat(realHtml, containsString("<head>"));
        assertThat(realHtml, containsString("<script>"));
        assertThat(realHtml, containsString("<style>"));
        
        assertThat("Should place screenshot 1 in same folder", new File(reportDirPath + "/screenshot-1.png").exists(), is(true));
        assertThat("Should place screenshot 2 in same folder", new File(reportDirPath + "/screenshot-2.png").exists(), is(true));
    }

    @Test public void shouldReport_toConsole_successfully() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ConsoleReportingListener listener = new ConsoleReportingListener(ps, ps);
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        listener.done();
        
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/console.txt")).replace("\\t    ", "\t");
        
        Assert.assertEquals(expectedText, baos.toString("UTF-8"));
    }

    private String bodyPart(String html) {
        int id1 = html.indexOf("    <body>");
        int id2 = html.indexOf("    </body>");
        return html.substring(id1 + 4, id2 + 11);
    }
}
