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
package net.mindengine.galen;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.EventHandler;
import net.mindengine.galen.runner.TestListener;
import net.mindengine.galen.runner.events.TestRetryEvent;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.tests.TestSession;

import java.util.Date;
import java.util.List;

/**
 * Used for running the test and invoking all test related events like: before, after, testRetry
 */
public class TestRunnable implements Runnable {
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
        catch(Throwable ex) {
            info.setException(ex);
            report.error(ex);
            ex.printStackTrace();
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
            shouldRetry = checkIfShouldRetry(info.getTest(), tries);
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
            e.printStackTrace();
        }
    }

    private void tellTestStarted(TestListener testListener, GalenTest test) {
        try {
            if (testListener != null) {
                testListener.onTestStarted(test);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
