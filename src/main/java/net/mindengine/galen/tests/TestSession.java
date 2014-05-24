/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.tests;

import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.CompleteListener;

public class TestSession {
    
    private static final ThreadLocal<TestSession> _sessions = new ThreadLocal<TestSession>();
    private GalenTestInfo testInfo;
    private GalenTest test;
    private Map<String, Object> data = new HashMap<String, Object>();
    private TestReport report;
    private CompleteListener listener;
    private GalenProperties properties = new GalenProperties();
    
    private TestSession(GalenTestInfo testInfo, GalenTest test) {
        this.setTestInfo(testInfo);
        this.setTest(test);
    }

    public static TestSession register(GalenTestInfo info, GalenTest test) {
        TestSession session = new TestSession(info, test);
        _sessions.set(session);
        
        return session;
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

    public void setReport(TestReport report) {
        this.report = report;
    }
    public TestReport getReport() {
        return this.report;
    }

    public void setListener(CompleteListener listener) {
        this.listener = listener;
    }
    public CompleteListener getListener() {
        return this.listener;
    }

    public GalenTest getTest() {
        return test;
    }

    public void setTest(GalenTest test) {
        this.test = test;
    }

    public GalenProperties getProperties() {
        return properties;
    }

    public void setProperties(GalenProperties properties) {
        this.properties = properties;
    }
}
