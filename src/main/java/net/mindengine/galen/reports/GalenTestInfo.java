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
package net.mindengine.galen.reports;

import java.lang.reflect.Method;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mindengine.galen.tests.GalenEmptyTest;
import net.mindengine.galen.tests.GalenTest;

public class GalenTestInfo {

    private String name;

    @JsonIgnore
    private TestReport report = new TestReport();

    @JsonIgnore
    private Throwable exception;
    private Date startedAt = new Date();
    private Date endedAt = new Date();

    @JsonIgnore
    private GalenTest test;
    
    
    public GalenTestInfo(String name, GalenTest test) {
        setName(name);
        setTest(test);
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

    public GalenTest getTest() {
        return test;
    }

    public void setTest(GalenTest test) {
        this.test = test;
    }

    public static GalenTestInfo fromString(String name) {
        return new GalenTestInfo(name, new GalenEmptyTest(name));
    }

    public static GalenTestInfo fromMethod(Method method) {
        String name = method.getDeclaringClass().getName() + "#" + method.getName();
        return GalenTestInfo.fromString(name);
    }

}
