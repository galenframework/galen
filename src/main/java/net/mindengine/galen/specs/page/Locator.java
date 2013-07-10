package net.mindengine.galen.specs.page;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19).append(locatorType).append(locatorValue).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Locator)) {
            return false;
        }
        Locator rhs = (Locator)obj;
        return new EqualsBuilder().append(locatorType, rhs.locatorType).append(locatorValue, rhs.locatorValue).isEquals();
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("locatorType", locatorType)
            .append("locatorValue", locatorValue)
            .toString();
    }
}
