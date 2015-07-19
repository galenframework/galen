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
package com.galenframework;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestReport;
import com.galenframework.runner.CompleteListener;
import com.galenframework.runner.EventHandler;
import com.galenframework.runner.TestListener;
import com.galenframework.tests.GalenTest;
import com.galenframework.tests.TestSession;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestReport;
import com.galenframework.runner.CompleteListener;
import com.galenframework.runner.EventHandler;
import com.galenframework.runner.TestListener;
import com.galenframework.runner.events.TestRetryEvent;
import com.galenframework.tests.GalenTest;
import com.galenframework.tests.TestSession;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for running the test and invoking all test related events like: before, after, testRetry
 */
public class TestRunnable implements Runnable {
    private final static Logger LOG = LoggerFactory.getLogger(TestRunnable.class);
    
    private final GalenTest test;
    private final CompleteListener listener;
    private final EventHandler eventHandler;
    private final List<GalenTestInfo> testInfos;

    public TestRunnable(GalenTest test, CompleteListener listener, EventHandler eventHandler, List<GalenTestInfo> testInfos) {
        this.test = test;
        this.listener = listener;
        this.eventHandler = eventHandler;
        this.testInfos = testInfos;
    }


    private GalenTestInfo runTest() {
        GalenTestInfo info = new GalenTestInfo(test.getName(), test);
        TestReport report = new TestReport();

        info.setStartedAt(new Date());
        info.setReport(report);


        TestSession session = TestSession.register(info);
        session.setReport(report);
        session.setListener(listener);

        eventHandler.invokeBeforeTestEvents(info);

        tellTestStarted(listener, test);
        try {
            test.execute(report, listener);
        }
        catch(Exception ex) {
            info.setException(ex);
            report.error(ex);
            if (listener != null) {
                listener.onGlobalError(ex);
            }
        }
        info.setEndedAt(new Date());

        eventHandler.invokeAfterTestEvents(info);
        tellTestFinished(listener, test);

        TestSession.clear();

        return info;
    }

    @Override
    public void run() {

        GalenTestInfo info = null;
        boolean shouldRetry = true;
        int tries = 1;
        while (shouldRetry) {
            info = runTest();
            if (info.isFailed()) {
                shouldRetry = checkIfShouldRetry(info.getTest(), tries);
            }
            else {
                shouldRetry = false;
            }
            tries++;
        }

        testInfos.add(info);
    }

    private boolean checkIfShouldRetry(GalenTest test, int tries) {
        for (TestRetryEvent retryEvent : eventHandler.getTestRetryEvents()) {
            if (retryEvent.shouldRetry(test, tries)) {
                return true;
            }
        }
        return false;
    }

    private void tellTestFinished(TestListener testListener, GalenTest test) {
        try {
            if (testListener != null) {
                testListener.onTestFinished(test);
            }
        }
        catch (Exception e) {
            LOG.error("Unkown error during test finishing", e);
        }
    }

    private void tellTestStarted(TestListener testListener, GalenTest test) {
        try {
            if (testListener != null) {
                testListener.onTestStarted(test);
            }
        }
        catch (Exception e) {
            LOG.error("Unkown error during test start", e);
        }
    }

}
