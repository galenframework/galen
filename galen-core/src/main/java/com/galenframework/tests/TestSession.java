/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestReport;
import com.galenframework.runner.CompleteListener;

public class TestSession {
    
    private static final ThreadLocal<TestSession> _sessions = new ThreadLocal<TestSession>();
    private static final ReentrantLock lock = new ReentrantLock();

    private GalenTestInfo testInfo;
    private Map<String, Object> data = new HashMap<String, Object>();
    private TestReport report = new TestReport();
    private CompleteListener listener;
    private GalenProperties properties = new GalenProperties();
    
    private TestSession(GalenTestInfo testInfo) {
        this.setTestInfo(testInfo);
    }

    public static TestSession register(GalenTestInfo info) {
        lock.lock();
        try {
            TestSession session = new TestSession(info);
            _sessions.set(session);
            return session;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            lock.unlock();
        }
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
        if (this.testInfo != null) {
            return this.testInfo.getTest();
        }
        else return null;
    }

    public GalenProperties getProperties() {
        return properties;
    }

    public void setProperties(GalenProperties properties) {
        this.properties = properties;
    }
}
