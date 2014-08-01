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
package net.mindengine.galen.reports;

import java.util.Date;

import net.mindengine.galen.tests.GalenTest;

public class GalenTestInfo {

    private String name;
    private TestReport report = new TestReport();
    private Throwable exception;
    private Date startedAt = new Date();
    private Date endedAt = new Date();
    private GalenTest testInstance;
    
    
    public GalenTestInfo(GalenTest test) {
        setTestInstance(test);
    }
    
    public boolean isFailed() {
        return exception != null || report.fetchStatistic().getErrors() > 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestReport getReport() {
        return report;
    }

    public void setReport(TestReport report) {
        this.report = report;
    }

    public void setException(Throwable ex) {
        this.exception = ex;
    }

    public Throwable getException() {
        return this.exception;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public GalenTest getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(GalenTest testInstance) {
        this.testInstance = testInstance;
    }
    
}
