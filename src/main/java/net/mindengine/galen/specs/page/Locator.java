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
package net.mindengine.galen.specs.page;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Locator {

    private String locatorType;
    private String locatorValue;
    private CorrectionsRect corrections;
    private int index = 0;

    public Locator(String locatorType, String locatorValue) {
        this(locatorType, locatorValue, 0);
    }
    public Locator(String locatorType, String locatorValue, int index) {
        this.setLocatorType(locatorType);
        this.setLocatorValue(locatorValue);
        this.setIndex(index);
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
        return new HashCodeBuilder(13, 19).append(locatorType).append(locatorValue).append(getCorrections()).toHashCode();
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
        return new EqualsBuilder().append(locatorType, rhs.locatorType).append(locatorValue, rhs.locatorValue).append(getCorrections(), rhs.getCorrections()).isEquals();
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("locatorType", locatorType)
            .append("locatorValue", locatorValue)
            .append("corrections", getCorrections())
            .toString();
    }

    public String prettyString() {
        return locatorType + ": " + locatorValue;
    }
    
    public Locator withCorrections(CorrectionsRect corrections) {
        this.setCorrections(corrections);
        return this;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public CorrectionsRect getCorrections() {
        return corrections;
    }
    public void setCorrections(CorrectionsRect corrections) {
        this.corrections = corrections;
    }
    
    public static Locator css(String cssText) {
        return new Locator("css", cssText, 0);
    }
    
    public static Locator xpath(String xpathText) {
        return new Locator("xpath", xpathText, 0);
    }
    public static Locator id(String idText) {
        return new Locator("id", idText);
    }
    
    
    public static Locator parse(String text) {
        int index = text.indexOf(":");
        if (index > 0) {
            String type = text.substring(0, index);
            if (type.equals("id")) {
                return id(text.substring(index + 1).trim());
            }
            else if(type.equals("xpath")) {
                return xpath(text.substring(index + 1).trim());
            }
            else if (type.equals("css")) {
                return css(text.substring(index + 1).trim());
            }
        }
        return css(text);
    }

}
