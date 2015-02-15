package net.mindengine.galen.reports.json;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;

/**
 * Created by ishubin on 2015/02/15.
 */
public class JsonTestReport {
    private final String testId;
    private String name;
    private TestReport report;

    public JsonTestReport(String testId, GalenTestInfo testInfo) {
        this.testId = testId;
        this.name = testInfo.getName();
        this.report = testInfo.getReport();
    }

    public String getName() {
        return name;
    }

    public TestReport getReport() {
        return report;
    }

    public String getTestId() {
        return testId;
    }
}
