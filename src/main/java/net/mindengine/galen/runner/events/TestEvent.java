package net.mindengine.galen.runner.events;

import net.mindengine.galen.reports.GalenTestInfo;

public interface TestEvent {

    public void execute(GalenTestInfo testInfo);
}
