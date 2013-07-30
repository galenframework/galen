/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.page.Rect;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Locator {

    private String locatorType;
    private String locatorValue;
    private Rect corrections;

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
        return new HashCodeBuilder(13, 19).append(locatorType).append(locatorValue).append(corrections).toHashCode();
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
        return new EqualsBuilder().append(locatorType, rhs.locatorType).append(locatorValue, rhs.locatorValue).append(corrections, rhs.corrections).isEquals();
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("locatorType", locatorType)
            .append("locatorValue", locatorValue)
            .append("corrections", corrections)
            .toString();
    }

    public Locator withCorrections(int left, int top, int width, int height) {
        this.setCorrections(new Rect(left, top, width, height));
        return this;
    }

    public Rect getCorrections() {
        return corrections;
    }

    public void setCorrections(Rect corrections) {
        this.corrections = corrections;
    }

    public Locator withCorrections(Rect corrections) {
        setCorrections(corrections);
        return this;
    }

}
