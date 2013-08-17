package net.mindengine.galen.tests.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.mindengine.galen.components.report.ReportingListenerTestUtils;
import net.mindengine.galen.reports.ConsoleReportingListener;
import net.mindengine.galen.reports.HtmlReportingListener;
import net.mindengine.galen.reports.TestngReportingListener;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

public class ReportingListenerTest {

    
    @Test public void shouldReport_inTestNgFormat_successfully() throws IOException {
        
        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "T00:00:00Z";
        
        TestngReportingListener listener = new TestngReportingListener();
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
        assertThat(listener.toXml() .replaceAll("T([0-9]{2}:){2}[0-9]{2}Z", "T00:00:00Z"), 
                is(expectedXml.replace("{expected-date}", expectedDate)));
    }
    
    
    @Test public void shouldReport_inHtmlFormat_successfully() throws IOException {
        HtmlReportingListener listener = new HtmlReportingListener();
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/html-report-suffix.html"));
        assertThat(bodyPart(listener.toHtml()), is(expectedHtml));
    }

    @Test public void shouldReport_toConsole_successfully() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ConsoleReportingListener listener = new ConsoleReportingListener(ps, ps);
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/console.txt"));
        assertThat(baos.toString("UTF-8"), is(expectedText));
    }

    private String bodyPart(String plainHtml) {
        int id1 = plainHtml.indexOf("<body>");
        int id2 = plainHtml.indexOf("</body>");
        
        return plainHtml.substring(id1, id2 + 7);
    }
    
    //TODO implement a .gtest file 
    
    //TODO run recursively all galen tests
}
