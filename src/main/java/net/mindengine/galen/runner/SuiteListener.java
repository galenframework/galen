package net.mindengine.galen.runner;

import java.util.List;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.tests.GalenTest;

public interface SuiteListener {

    public void beforeTestSuite(List<GalenTest> tests);
    
    public void afterTestSuite(List<GalenTestInfo> tests);
}
