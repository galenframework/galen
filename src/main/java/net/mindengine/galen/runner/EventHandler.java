/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.runner;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.runner.events.TestEvent;
import net.mindengine.galen.runner.events.TestFilterEvent;
import net.mindengine.galen.runner.events.TestRetryEvent;
import net.mindengine.galen.runner.events.TestSuiteEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    private List<TestEvent> beforeTestEvents = new LinkedList<TestEvent>();
    private List<TestEvent> afterTestEvents = new LinkedList<TestEvent>();
    private List<TestFilterEvent> testFilterEvents = new LinkedList<TestFilterEvent>();
    private List<TestRetryEvent> testRetryEvents = new LinkedList<TestRetryEvent>();

    private List<TestSuiteEvent> beforeTestSuiteEvents = new LinkedList<TestSuiteEvent>();
    private List<TestSuiteEvent> afterTestSuiteEvents = new LinkedList<TestSuiteEvent>();

    public List<TestEvent> getBeforeTestEvents() {
        return beforeTestEvents;
    }

    public void setBeforeTestEvents(final List<TestEvent> beforeTestEvents) {
        this.beforeTestEvents = beforeTestEvents;
    }

    public List<TestEvent> getAfterTestEvents() {
        return afterTestEvents;
    }

    public void setAfterTestEvents(final List<TestEvent> afterTestEvents) {
        this.afterTestEvents = afterTestEvents;
    }

    public List<TestSuiteEvent> getAfterTestSuiteEvents() {
        return afterTestSuiteEvents;
    }

    public void setAfterTestSuiteEvents(final List<TestSuiteEvent> afterTestSuiteEvents) {
        this.afterTestSuiteEvents = afterTestSuiteEvents;
    }

    public List<TestSuiteEvent> getBeforeTestSuiteEvents() {
        return beforeTestSuiteEvents;
    }

    public void setBeforeTestSuiteEvents(final List<TestSuiteEvent> beforeTestSuiteEvents) {
        this.beforeTestSuiteEvents = beforeTestSuiteEvents;
    }

    public void invokeBeforeTestSuiteEvents() {
        execute(getBeforeTestSuiteEvents());
    }

    public void invokeAfterTestSuiteEvents() {
        execute(getAfterTestSuiteEvents());
    }

    private void execute(final List<TestSuiteEvent> events) {
        if (events != null) {
            for (final TestSuiteEvent event : events) {
                if (event != null) {
                    try {
                        event.execute();
                    } catch (final Exception ex) {
                        LOG.error("Unknow error during executing test suites.", ex);
                    }
                }
            }
        }
    }

    public void invokeBeforeTestEvents(final GalenTestInfo testInfo) {
        execute(getBeforeTestEvents(), testInfo);
    }

    public void invokeAfterTestEvents(final GalenTestInfo testInfo) {
        execute(getAfterTestEvents(), testInfo);
    }

    private void execute(final List<TestEvent> events, final GalenTestInfo testInfo) {
        if (events != null) {
            for (final TestEvent event : events) {
                if (event != null) {
                    try {
                        event.execute(testInfo);
                    } catch (final Exception ex) {
                        LOG.error("Unknow error during executing test events.", ex);
                    }
                }
            }
        }
    }

    public List<TestFilterEvent> getTestFilterEvents() {
        return testFilterEvents;
    }

    public void setTestFilterEvents(final List<TestFilterEvent> testFilterEvents) {
        this.testFilterEvents = testFilterEvents;
    }

    public List<TestRetryEvent> getTestRetryEvents() {
        return testRetryEvents;
    }

    public void setTestRetryEvents(final List<TestRetryEvent> testRetryEvents) {
        this.testRetryEvents = testRetryEvents;
    }
}
