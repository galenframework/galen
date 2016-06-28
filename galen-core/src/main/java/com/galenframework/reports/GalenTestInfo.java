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
package com.galenframework.reports;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.galenframework.tests.GalenEmptyTest;
import com.galenframework.tests.GalenTest;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringEscapeUtils;

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

    public static GalenTestInfo fromString(final String name) {
        return new GalenTestInfo(name, new GalenEmptyTest(name, null));
    }

    public static GalenTestInfo fromString(final String name, final List<String> groups) {
        return new GalenTestInfo(name, new GalenEmptyTest(name, groups));
    }

    public static GalenTestInfo fromMethod(Method method) {
        String name = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
        return GalenTestInfo.fromString(name);
    }

    public static GalenTestInfo fromMethod(Method method, Object[] arguments) {
        StringBuilder builder = new StringBuilder(method.getDeclaringClass().getSimpleName() + "#" + method.getName());
        if (arguments != null && arguments.length > 0) {
            builder.append(" (");
            boolean shouldUseComma = false;
            for (Object argument : arguments) {
                if (shouldUseComma) {
                    builder.append(", ");
                }
                builder.append(convertArgumentToString(argument));
                shouldUseComma = true;
            }
            builder.append(") ");
        }
        return GalenTestInfo.fromString(builder.toString());
    }

    private static String convertArgumentToString(Object argument) {
        if (argument == null) {
            return "null";
        } else if (argument instanceof String) {
            return "\"" + StringEscapeUtils.escapeJava(argument.toString()) + "\"";
        } else if (argument instanceof Boolean) {
            return Boolean.toString((Boolean)argument);
        } else {
            return argument.toString();
        }
    }
}
