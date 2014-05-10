package net.mindengine.galen.reports;


public class GalenTestAggregatedInfo {

    private GalenTestInfo testInfo;
    private TestStatistic statistic;
    private String testId;
    

    public GalenTestAggregatedInfo(String testId, GalenTestInfo test) {
        this.setTestInfo(test);
        this.setStatistic(test.getReport().fetchStatistic());
        this.setTestId(testId);
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

}
