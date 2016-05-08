/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.specs;

import static java.lang.String.format;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Range {

    public Range(RangeValue from, RangeValue to) {
        this.from = from;
        this.to = to;
    }

    private RangeValue from;
    private RangeValue to;
    private String percentageOfValue;
    private RangeType rangeType = RangeType.BETWEEN;

    public int findPrecision() {
        int precision1 = 0;
        int precision2 = 0;
        if (from != null) {
            precision1 = from.getPrecision();
        }
        if (to != null) {
            precision2 = to.getPrecision();
        }
        return Math.max(precision1, precision2);
    }

    public enum RangeType {
        BETWEEN, EXACT, GREATER_THAN, LESS_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN_OR_EQUALS
    }
    
    public RangeValue getFrom() {
        return from;
    }
    public RangeValue getTo() {
        return to;
    }
    public static Range exact(RangeValue number) {
        return new Range(number, number).withType(RangeType.EXACT);
    }
    public Range withType(RangeType rangeType) {
        setRangeType(rangeType);
        return this;
    }
    public static Range between(RangeValue from, RangeValue to) {
        return new Range(from, to).withType(RangeType.BETWEEN);
    }

    public static Range between(int from, int to) {
        return between(new RangeValue(from), new RangeValue(to));
    }

    public static Range exact(int value) {
        return exact(new RangeValue(value));
    }

    public static Range greaterThan(int value) {
        return greaterThan(new RangeValue(value));
    }

    public static Range lessThan(int value) {
        return lessThan(new RangeValue(value));
    }

    public static Range lessThanOrEquals(int value) {
        return lessThanOrEquals(new RangeValue(value));
    }

    public static Range greaterThanOrEquals(int value) {
        return greaterThanOrEquals(new RangeValue(value));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19)
            .append(from)
            .append(to)
            .append(percentageOfValue)
            .append(rangeType)
            .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range rhs = (Range)obj;
        return new EqualsBuilder()
            .append(from, rhs.from)
            .append(to, rhs.to)
            .append(percentageOfValue, rhs.percentageOfValue)
            .append(rangeType, rhs.rangeType)
            .isEquals();
    }
    
    @Override
    public String toString() {
        String withPercentage = "";
        if (percentageOfValue != null) {
            withPercentage = " % of " + percentageOfValue;
        }
        return format("Range{%s%s}", prettyString(), withPercentage);
    }
    public boolean holds(double offset) {
    	if (rangeType == RangeType.GREATER_THAN) {
            return from.isLessThan(offset);
    	}
        else if (rangeType == RangeType.GREATER_THAN_OR_EQUALS) {
            return from.isLessThanOrEquals(offset);
        }
    	else if (rangeType == RangeType.LESS_THAN) {
            return to.isGreaterThan(offset);
        }
        else if (rangeType == RangeType.LESS_THAN_OR_EQUALS) {
            return to.isGreaterThanOrEquals(offset);
    	} else {
            return from.isLessThanOrEquals(offset) && to.isGreaterThanOrEquals(offset);
        }
    }
    public String prettyString() {
        return prettyString("px");
    }
    
    public String prettyString(String dimension) {
        if (rangeType == RangeType.EXACT) {
            return String.format("%s%s", from.toString(), dimension);
        }
        else if (rangeType == RangeType.GREATER_THAN) {
            return String.format("> %s%s", from.toString(), dimension);
        }
        else if (rangeType == RangeType.LESS_THAN) {
            return String.format("< %s%s", to.toString(), dimension);
        }
        else if (rangeType == RangeType.LESS_THAN_OR_EQUALS) {
            return String.format("<= %s%s", to.toString(), dimension);
        }
        else if (rangeType == RangeType.GREATER_THAN_OR_EQUALS) {
            return String.format(">= %s%s", from.toString(), dimension);
        }
        else return String.format("%s to %s%s", from.toString(), to.toString(), dimension);
    }

	public Range withPercentOf(String percentageOfValue) {
        this.setPercentageOfValue(percentageOfValue);
        return this;
    }
    public String getPercentageOfValue() {
        return percentageOfValue;
    }
    public void setPercentageOfValue(String percentageOfValue) {
        this.percentageOfValue = percentageOfValue;
    }
    public boolean isPercentage() {
        return percentageOfValue != null && !percentageOfValue.isEmpty();
    }
	public static Range greaterThan(RangeValue value) {
		return new Range(value, null).withType(RangeType.GREATER_THAN);
	}
	public static Range lessThan(RangeValue value) {
		return new Range(null, value).withType(RangeType.LESS_THAN);
	}

    public static Range lessThanOrEquals(RangeValue value) {
        return new Range(null, value).withType(RangeType.LESS_THAN_OR_EQUALS);
    }

    public static Range greaterThanOrEquals(RangeValue value) {
        return new Range(value, null).withType(RangeType.GREATER_THAN_OR_EQUALS);
    }

    public RangeType getRangeType() {
        return rangeType;
    }
    public void setRangeType(RangeType rangeType) {
        this.rangeType = rangeType;
    }
    
    public String getErrorMessageSuffix() {
        if (isPercentage()) {
            return getErrorMessageSuffix("%");
        } else {
            return getErrorMessageSuffix("px");
        }
    }
    public String getErrorMessageSuffix(String dimension) {
        if (rangeType == RangeType.EXACT) {
            return String.format("instead of %s", prettyString(dimension));
        }
        else if (rangeType == RangeType.BETWEEN) {
            return String.format("which is not in range of %s", prettyString(dimension));
        }
        else if (rangeType == RangeType.GREATER_THAN) {
            return String.format("but it should be greater than %s%s", from.toString(), dimension);
        }
        else if (rangeType == RangeType.LESS_THAN) {
            return String.format("but it should be less than %s%s", to.toString(), dimension);
        }
        else return "but the expected range is unknown";
    }
    
}
