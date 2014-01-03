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

}
