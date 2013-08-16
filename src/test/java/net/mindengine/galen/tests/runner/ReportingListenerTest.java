package net.mindengine.galen.tests.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.mindengine.galen.components.report.ReportingListenerTestUtils;
import net.mindengine.galen.reports.HtmlReportingListener;
import net.mindengine.galen.reports.TestngReportingListener;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class ReportingListenerTest {

    
    @Test public void shouldReport_inTestNgFormat_successfully() throws IOException {
        
        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "T00:00:00Z";
        
        TestngReportingListener listener = new TestngReportingListener();
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedXml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/testng-report.xml"));
        assertThat(listener.toXml()
                .replaceAll("T([0-9]{2}:){2}[0-9]{2}Z", "T00:00:00Z")
                .replaceAll("\\s+", " "), is(expectedXml.replace("{expected-date}", expectedDate).replaceAll("\\s+", " ")));
    }
    
    
    @Test public void shouldReport_inHtmlFormat_successfully() throws IOException {
        HtmlReportingListener listener = new HtmlReportingListener();
        ReportingListenerTestUtils.performSampleReporting(listener, listener);
        
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("/expected-reports/html-report-suffix.html"));
        assertThat(listener.toHtml().replaceAll("\\s+", " "), endsWith(expectedHtml.replaceAll("\\s+", " ")));
    }
    
    //TODO finish html reporting listener
    
    //TODO implement console reporting listener
    
    //TODO implement a *.gtest file 
    
    //TODO run recursively all galen tests
}
