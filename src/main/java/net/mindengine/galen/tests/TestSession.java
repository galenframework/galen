package net.mindengine.galen.tests;

import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.reports.GalenTestInfo;

public class TestSession {
    
    private static final ThreadLocal<TestSession> _sessions = new ThreadLocal<TestSession>();
    private GalenTestInfo testInfo;
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private TestSession(GalenTestInfo testInfo) {
        this.setTestInfo(testInfo);
    }

    public static void register(GalenTestInfo info) {
        _sessions.set(new TestSession(info));
    }


    public static void clear() {
        _sessions.remove();
    }

    public GalenTestInfo getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(GalenTestInfo testInfo) {
        this.testInfo = testInfo;
    }
    
    public static TestSession current() {
        return _sessions.get();
    }
    
    public void put(String name, Object value) {
        this.data.put(name, value);
    }
    
    public Object get(String name) {
        return this.data.get(name);
    }

}
