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

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class GalenTestAggregatedInfo {

    @JsonUnwrapped
    private GalenTestInfo testInfo;
    private TestStatistic statistic;
    private String testId;



    public GalenTestAggregatedInfo(String testId, GalenTestInfo test) {
        this.setTestInfo(test);
        this.setStatistic(test.getReport().fetchStatistic());
        this.setTestId(testId);
    }

    
    public boolean getFailed() {
        return testInfo.getException() != null || statistic.getErrors() > 0;
    }
    
    public GalenTestInfo getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(GalenTestInfo testInfo) {
        this.testInfo = testInfo;
    }

    public TestStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(TestStatistic statistic) {
        this.statistic = statistic;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getExceptionMessage() {
        if (testInfo.getException() != null) {
            return ExceptionUtils.getMessage(testInfo.getException());
        }
        return null;
    }

    public String getExceptionStacktrace() {
        if (testInfo.getException() != null) {
            return ExceptionUtils.getStackTrace(testInfo.getException());
        }
        return null;
    }
    
    public Long getDuration() {
        return testInfo.getEndedAt().getTime() - testInfo.getStartedAt().getTime();
    }

    public List<String> getGroups() {
        if (testInfo.getTest() != null) {
            return testInfo.getTest().getGroups();
        }
        return null;
    }
}