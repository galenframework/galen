package net.mindengine.galen.tests;

import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.CompleteListener;

public interface GalenTest {

    public String getName();
    public TestReport execute(CompleteListener listener);
}
