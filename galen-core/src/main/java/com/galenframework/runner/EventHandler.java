/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.runner;

import java.util.LinkedList;
import java.util.List;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.runner.events.TestEvent;
import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.runner.events.TestSuiteEvent;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.runner.events.TestEvent;
import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.runner.events.TestRetryEvent;
import com.galenframework.runner.events.TestSuiteEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    private List<TestEvent> beforeTestEvents = new LinkedList<>();
    private List<TestEvent> afterTestEvents = new LinkedList<>();
    private List<TestFilterEvent> testFilterEvents = new LinkedList<>();
    private List<TestRetryEvent> testRetryEvents = new LinkedList<>();

    private List<TestSuiteEvent> beforeTestSuiteEvents = new LinkedList<>();
    private List<TestSuiteEvent> afterTestSuiteEvents = new LinkedList<>();

    public List<TestEvent> getBeforeTestEvents() {
        return beforeTestEvents;
    }

    public void setBeforeTestEvents(List<TestEvent> beforeTestEvents) {
        this.beforeTestEvents = beforeTestEvents;
    }

    public List<TestEvent> getAfterTestEvents() {
        return afterTestEvents;
    }

    public void setAfterTestEvents(List<TestEvent> afterTestEvents) {
        this.afterTestEvents = afterTestEvents;
    }

    public List<TestSuiteEvent> getAfterTestSuiteEvents() {
        return afterTestSuiteEvents;
    }

    public void setAfterTestSuiteEvents(List<TestSuiteEvent> afterTestSuiteEvents) {
        this.afterTestSuiteEvents = afterTestSuiteEvents;
    }

    public List<TestSuiteEvent> getBeforeTestSuiteEvents() {
        return beforeTestSuiteEvents;
    }

    public void setBeforeTestSuiteEvents(List<TestSuiteEvent> beforeTestSuiteEvents) {
        this.beforeTestSuiteEvents = beforeTestSuiteEvents;
    }

    public void invokeBeforeTestSuiteEvents() {
        execute(getBeforeTestSuiteEvents());
    }

    public void invokeAfterTestSuiteEvents() {
        execute(getAfterTestSuiteEvents());
    }

    private void execute(List<TestSuiteEvent> events) {
        if (events != null) {
            for (TestSuiteEvent event : events) {
                if (event != null) {
                    try {
                        event.execute();
                    } catch (Throwable ex) {
                        LOG.error("Unknow error during executing test suites.", ex);
                    }
                }
            }
        }
    }

    public void invokeBeforeTestEvents(GalenTestInfo testInfo) {
        execute(getBeforeTestEvents(), testInfo);
    }

    public void invokeAfterTestEvents(GalenTestInfo testInfo) {
        execute(getAfterTestEvents(), testInfo);
    }

    private void execute(List<TestEvent> events, GalenTestInfo testInfo) {
        if (events != null) {
            for (TestEvent event : events) {
                if (event != null) {
                    try {
                        event.execute(testInfo);
                    } catch (Throwable ex) {
                        LOG.error("Unknow error during executing test events.", ex);
                    }
                }
            }
        }
    }

    public List<TestFilterEvent> getTestFilterEvents() {
        return testFilterEvents;
    }

    public void setTestFilterEvents(List<TestFilterEvent> testFilterEvents) {
        this.testFilterEvents = testFilterEvents;
    }

    public List<TestRetryEvent> getTestRetryEvents() {
        return testRetryEvents;
    }

    public void setTestRetryEvents(List<TestRetryEvent> testRetryEvents) {
        this.testRetryEvents = testRetryEvents;
    }
}
