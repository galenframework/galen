package com.galenframework.tests.reports;

import com.galenframework.reports.TestReport;
import com.galenframework.reports.nodes.*;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestReportTest {


    @Test
    public void shouldAllow_toStore_extrasData() {
        TestReport report = new TestReport();

        report.info("Some info")
                .withExtrasText("debug-message", "some debug value")
                .withExtrasLink("link", "http://example.com")
                .withExtrasFile("someFile", new File(getClass().getResource("/some-report-attachment.txt").getFile()))
                .withExtrasImage("screenshot", new File(getClass().getResource("/imgs/page-screenshot.png").getFile()));

        Map<String, ReportExtra> extras = report.getNodes().get(0).getExtras();

        ReportExtraText extraText = (ReportExtraText) extras.get("debug-message");
        assertThat(extraText.getValue(), is("some debug value"));

        ReportExtraLink extraLink = (ReportExtraLink) extras.get("link");
        assertThat(extraLink.getValue(), is("http://example.com"));

        ReportExtraFile extraFile = (ReportExtraFile) extras.get("someFile");
        assertThat(extraFile.getValue(), is("file-1-some-report-attachment.txt"));

        ReportExtraImage extraImage = (ReportExtraImage) extras.get("screenshot");
        assertThat(extraImage.getValue(), is("file-2-page-screenshot.png"));


    }
}
