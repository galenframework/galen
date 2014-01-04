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
package net.mindengine.galen.specs;

import static java.lang.String.format;

import java.text.DecimalFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Range {

    private Range(Double from, Double to) {
        this.from = from;
        this.to = to;
    }
    
    private Double from;
    private Double to;
    private String percentageOfValue;
    private RangeType rangeType = RangeType.BETWEEN;
    
    public enum RangeType {
        BETWEEN, EXACT, GREATER_THAN, LESS_THAN
    }
    
    public Double getFrom() {
        return from;
    }
    public Double getTo() {
        return to;
    }
    public static Range exact(double number) {
        return new Range(number, number).withType(RangeType.EXACT);
    }
    public Range withType(RangeType rangeType) {
        setRangeType(rangeType);
        return this;
    }
    public static Range between(double from, double to) {
        return new Range(Math.min(from, to), Math.max(from, to)).withType(RangeType.BETWEEN);
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
    public boolean isExact() {
        return rangeType == RangeType.EXACT;
    }
    public boolean holds(double offset) {
    	if (isGreaterThan()) {
    		return offset > from;
    	}
    	else if (isLessThan()) {
    		return offset < to;
    	}
    	else return offset >= from && offset <= to;
    }
    public static String doubleToString(Double value) {
        if (value != null) {
            return new DecimalFormat("#.##").format(value);
        }
        else return "null";
    }
    public String prettyString() {
        return prettyString("px");
    }
    
    private String prettyString(String dimension) {
        if (isExact()) {
            return String.format("%s%s", doubleToString(from), dimension);
        }
        else if (isGreaterThan()) {
            return String.format("> %s%s", doubleToString(from), dimension);
        }
        else if (isLessThan()) {
            return String.format("< %s%s", doubleToString(to), dimension);
        }
        else return String.format("%s to %s%s", doubleToString(from), doubleToString(to), dimension);
    }
    
    private boolean isLessThan() {
		return rangeType == RangeType.LESS_THAN;
	}
	private boolean isGreaterThan() {
	    return rangeType == RangeType.GREATER_THAN;
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
	public static Range greaterThan(Double value) {
		return new Range(value, null).withType(RangeType.GREATER_THAN);
	}
	public static Range lessThan(Double value) {
		return new Range(null, value).withType(RangeType.LESS_THAN);
	}
    public RangeType getRangeType() {
        return rangeType;
    }
    public void setRangeType(RangeType rangeType) {
        this.rangeType = rangeType;
    }
    
    public String getErrorMessageSuffix() {
        return getErrorMessageSuffix("px");
    }
    public String getErrorMessageSuffix(String dimension) {
        if (rangeType == RangeType.EXACT) {
            return String.format("instead of %s", prettyString(dimension));
        }
        else if (rangeType == RangeType.BETWEEN) {
            return String.format("which is not in range of %s", prettyString(dimension));
        }
        else if (rangeType == RangeType.GREATER_THAN) {
            return String.format("but it should be greater than %s%s", doubleToString(from), dimension);
        }
        else if (rangeType == RangeType.LESS_THAN) {
            return String.format("but it should be less than %s%s", doubleToString(to), dimension);
        }
        else return "but the expected range is unknown";
    }
    
}
