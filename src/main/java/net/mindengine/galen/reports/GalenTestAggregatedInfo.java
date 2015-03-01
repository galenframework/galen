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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.nodes.LayoutReportNode;
import net.mindengine.galen.reports.nodes.TestReportNode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class GalenTestAggregatedInfo {

    @JsonUnwrapped
    private GalenTestInfo testInfo;
    private TestStatistic statistic;
    private String testId;
    private Set<String> includedTags = new TreeSet<String>();
    private Set<String> excludedTags = new TreeSet<String>();


    public GalenTestAggregatedInfo(String testId, GalenTestInfo test) {
        this.setTestInfo(test);
        
        final List<TestReportNode> reportNodes= test.getReport().getNodes();
        if (!CollectionUtils.isEmpty(reportNodes)) {
            for (final TestReportNode testReportNode : reportNodes) {
                if (testReportNode instanceof LayoutReportNode) {
                    final LayoutReport layoutReport = ((LayoutReportNode) testReportNode).getLayoutReport();
                    if (!CollectionUtils.isEmpty(layoutReport.getIncludedTags())) {
                        this.includedTags.addAll(layoutReport.getIncludedTags());
                    }
                    if (!CollectionUtils.isEmpty(layoutReport.getExcludedTags())) {
                        this.excludedTags.addAll(layoutReport.getExcludedTags());
                    }
                }
            }
        }
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

    public Set<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(Set<String> includedTags) {
        this.includedTags = includedTags;
    }

    public Set<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(Set<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public String getTagsPretty() {
        final StringBuffer tagsPretty = new StringBuffer();
        if (!CollectionUtils.isEmpty(this.includedTags)) {
            final Set<String> allUsedTags = new TreeSet<String>(this.includedTags);
            if (!CollectionUtils.isEmpty(this.excludedTags)) {
                allUsedTags.removeAll(this.excludedTags);
            }
            final Iterator<String> it = allUsedTags.iterator();
            for (;;) {
                final String tag  = it.next();
                tagsPretty.append(tag);
                if (! it.hasNext()){
                    return tagsPretty.toString();
                } else {
                    tagsPretty.append(", ");
                }
            }
        }
        // fallback on empty
        return tagsPretty.toString();
    }
    
    public String getDurationPretty() {
        if (testInfo.getStartedAt() != null && testInfo.getEndedAt() != null) {
            Long durationInSeconds = (testInfo.getEndedAt().getTime() - testInfo.getStartedAt().getTime())/1000;

            if (durationInSeconds > 0) {
                Long hours = durationInSeconds / 3600;
                Long minutes = (durationInSeconds - hours * 3600) / 60;
                Long seconds = durationInSeconds - hours * 3600 - minutes * 60;

                StringBuilder builder = new StringBuilder();
                if (hours > 0) {
                    builder.append(Long.toString(hours));
                    builder.append('h');
                }

                if (minutes > 0 || hours > 0) {
                    if (hours > 0) {
                        builder.append(' ');
                    }
                    builder.append(Long.toString(minutes));
                    builder.append('m');
                }

                if (seconds > 0) {
                    if (hours > 0 || minutes > 0) {
                        builder.append(' ');
                    }
                    builder.append(Long.toString(seconds));
                    builder.append('s');
                }

                return builder.toString();
            }
        }
        return "-";
    }
}
