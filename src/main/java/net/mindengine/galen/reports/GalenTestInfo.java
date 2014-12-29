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

import net.mindengine.galen.tests.GalenEmptyTest;
import net.mindengine.galen.tests.GalenTest;

public class GalenTestInfo {

	private String name;
    private String group;
    private TestReport report = new TestReport();
    private Throwable exception;
    private Date startedAt = new Date();
    private Date endedAt = new Date();
    private GalenTest test;
    

    
    /**
     * Creates Galen test info
     * @param pName name of the test
     */
    public GalenTestInfo(final String pName) {
        setName(pName);
        setTest(new GalenEmptyTest(pName));
    }

    /**
     * Creates Galen test info
     * @param pGroup name of a test group
     * @param pName name of the test
     */
    public GalenTestInfo(final String pGroup, final String pName) {
        setGroup(pGroup);
        setName(pName);
        setTest(new GalenEmptyTest(pName));
    }
    
    /**
     * Creates Galen test info
     * @param pName name of the test
     * @param pTest test reference
     */
    public GalenTestInfo(final String pName, final GalenTest pTest) {
        setName(pName);
        setTest(pTest);
    }
    
    /**
     * Creates Galen test info
     * @param pGroup name of a test group
     * @param pName name of the test
     * @param pTest test reference
     */
    public GalenTestInfo(final String pGroup, final String pName, final GalenTest pTest) {
        setName(pName);
        setGroup(pGroup);
        setTest(pTest);
    }
    
    public boolean isFailed() {
        return exception != null || report.fetchStatistic().getErrors() > 0;
    }

	public String getGroup() {
		return group;
	}

	public void setGroup(final String pGroup) {
		this.group = pGroup;
	}

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        this.name = pName;
    }

    public TestReport getReport() {
        return report;
    }

    public void setReport(final TestReport pReport) {
        this.report = pReport;
    }

    public void setException(final Throwable pThrowable) {
        this.exception = pThrowable;
    }

    public Throwable getException() {
        return this.exception;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(final Date pStart) {
        this.startedAt = pStart;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(final Date pEnd) {
        this.endedAt = pEnd;
    }

    public GalenTest getTest() {
        return test;
    }

    public void setTest(final GalenTest pTest) {
        this.test = pTest;
    }


    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GalenTestInfo [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (group != null) {
			builder.append("group=");
			builder.append(group);
			builder.append(", ");
		}
		if (report != null) {
			builder.append("report=");
			builder.append(report);
			builder.append(", ");
		}
		if (exception != null) {
			builder.append("exception=");
			builder.append(exception);
			builder.append(", ");
		}
		if (startedAt != null) {
			builder.append("startedAt=");
			builder.append(startedAt);
			builder.append(", ");
		}
		if (endedAt != null) {
			builder.append("endedAt=");
			builder.append(endedAt);
			builder.append(", ");
		}
		if (test != null) {
			builder.append("test=");
			builder.append(test);
		}
		builder.append("]");
		return builder.toString();
	}
}
