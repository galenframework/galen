package net.mindengine.galen.specs.page;

public class Locator {

    private String locatorType;
    private String locatorValue;

    public Locator(String locatorType, String locatorValue) {
        this.setLocatorType(locatorType);
        this.setLocatorValue(locatorValue);
    }

    public String getLocatorType() {
        return locatorType;
    }

    public void setLocatorType(String locatorType) {
        this.locatorType = locatorType;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }

}
