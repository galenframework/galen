package net.mindengine.galen.reports;

public class GalenTestAggregatedInfo {

    private GalenTestInfo testInfo;
    private TestStatistic statistic;
    private String testId;
    
    private static int _uniqueId = 0;

    public GalenTestAggregatedInfo(GalenTestInfo test) {
        this.setTestInfo(test);
        this.setStatistic(test.getReport().fetchStatistic());
        this.setTestId(createTestId(test.getName()));
    }

    private static synchronized String createTestId(String name) {
        _uniqueId++;
        return String.format("report-%d-%s", _uniqueId, convertToFileName(name));
    }
    
    private static String convertToFileName(String name) {
        return name.toLowerCase().replaceAll("[^\\dA-Za-z\\.\\-]", " ").replaceAll("\\s+", "-");
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
