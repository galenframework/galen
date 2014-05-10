package net.mindengine.galen.reports;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.exception.ExceptionUtils;


public class GalenTestAggregatedInfo {

    private GalenTestInfo testInfo;
    private TestStatistic statistic;
    private String testId;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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
        return ExceptionUtils.getMessage(testInfo.getException());
    }
    
    public String getExceptionStacktrace() {
        return ExceptionUtils.getStackTrace(testInfo.getException());
    }
    
    public Long getDuration() {
        return testInfo.getEndedAt().getTime() - testInfo.getStartedAt().getTime();
    }
    
    public String getStartedAtFormatted() {
        return sdf.format(testInfo.getStartedAt());
    }
    
    public String getEndedAtFormatted() {
        return sdf.format(testInfo.getEndedAt());
    }

}
