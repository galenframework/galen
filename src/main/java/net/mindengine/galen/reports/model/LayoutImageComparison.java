package net.mindengine.galen.reports.model;

/**
 * Created by ishubin on 2/17/15.
 */
public class LayoutImageComparison {

    private String actualImage;
    private String expectedImage;
    private String comparisonMapImage;

    public String getActualImage() {
        return actualImage;
    }

    public void setActualImage(String actualImage) {
        this.actualImage = actualImage;
    }

    public String getExpectedImage() {
        return expectedImage;
    }

    public void setExpectedImage(String expectedImage) {
        this.expectedImage = expectedImage;
    }

    public String getComparisonMapImage() {
        return comparisonMapImage;
    }

    public void setComparisonMapImage(String comparisonMapImage) {
        this.comparisonMapImage = comparisonMapImage;
    }
}
