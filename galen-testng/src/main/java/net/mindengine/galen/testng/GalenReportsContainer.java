package net.mindengine.galen.testng;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class GalenReportsContainer {

    private static final GalenReportsContainer _instance = new GalenReportsContainer();
    private final List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();

    private GalenReportsContainer() {
    }

    public static final GalenReportsContainer get() {
        return _instance;
    }

    public TestReport registerTest(Method method) {
        GalenTestInfo testInfo = GalenTestInfo.fromMethod(method);
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public List<GalenTestInfo> getAllTests() {
        return tests;
    }
}
