package net.mindengine.galen.testng;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.HtmlReportBuilder;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.List;

public class GalenReportingListener implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> iSuites, String s) {
        System.out.println("Generating Galen Html reports");
        List<GalenTestInfo> tests = GalenReportsContainer.get().getAllTests();

        try {
            new HtmlReportBuilder().build(tests, "target/galen-html-reports");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
