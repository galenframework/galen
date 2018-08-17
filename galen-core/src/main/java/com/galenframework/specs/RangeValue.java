/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RangeValue {
    private int value;
    private int precision = 0;


    public RangeValue(int value) {
        this.value = value;
    }
    public RangeValue(int value, int precision) {
        this.value = value;
        this.precision = precision;
    }
    public RangeValue(double value, int precision) {
        this.value = convertValue(value, precision);
        this.precision = precision;
    }

    private static int convertValue(double value, int precision) {
        if (value > 0) {
            return (int) Math.floor(value * Math.pow(10, precision));
        } else {
            return (int) Math.ceil(value * Math.pow(10, precision));
        }
    }

    public int asInt() {
        if (value > 0) {
            return (int) Math.floor(value / Math.pow(10, precision));
        } else {
            return (int) Math.ceil(value / Math.pow(10, precision));
        }
    }

    public double asDouble() {
        return ((double)value) / Math.pow(10, precision);
    }


    @Override
    public String toString() {
        if (precision > 0) {
            int d = (int) Math.pow(10, precision);

            int firstPart = (int) Math.floor(value / d);
            int secondPart = Math.abs(value % d);

            StringBuilder builder = new StringBuilder();
            builder.append(Integer.toString(firstPart));
            builder.append('.');

            String digits = Integer.toString(secondPart);
            for (int i = digits.length(); i < precision; i++) {
                builder.append('0');
            }
            builder.append(digits);
            return builder.toString();
        } else {
            return Integer.toString(value);
        }
    }

    public boolean equalsTo(int otherValue) {
        return this.value == convertValue(otherValue, precision);
    }

    public boolean equalsTo(double otherValue) {
        return this.value == convertValue(otherValue, precision);
    }

    public boolean isLessThanOrEquals(int otherValue) {
        return this.value <= convertValue(otherValue, precision);
    }

    public boolean isLessThanOrEquals(double otherValue) {
        return this.value <= convertValue(otherValue, precision);
    }

    public boolean isLessThan(double otherValue) {
        return this.value < convertValue(otherValue, precision);
    }

    public boolean isGreaterThan(double otherValue) {
        return this.value > convertValue(otherValue, precision);
    }


    public boolean isGreaterThanOrEquals(int otherValue) {
        return this.value >= convertValue(otherValue, precision);
    }

    public boolean isGreaterThanOrEquals(double otherValue) {
        return this.value >= convertValue(otherValue, precision);
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19)
            .append(value)
            .append(precision)
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
        if (!(obj instanceof RangeValue)) {
            return false;
        }
        RangeValue rhs = (RangeValue)obj;
        return new EqualsBuilder()
                .append(this.value, rhs.value)
                .append(this.precision, rhs.precision)
                .isEquals();
    }

    public int getPrecision() {
        return precision;
    }

    public static RangeValue parseRangeValue(String text) {
        int pointIndex = text.indexOf('.');
        if (pointIndex > 0) {
            String firstPart = text.substring(0, pointIndex).trim();
            String secondPart = text.substring(pointIndex + 1).trim();

            int precision = secondPart.length();
            int power = (int) Math.pow(10, precision);

            int mainValue = Integer.parseInt(firstPart) * power;
            int addedValue = Integer.parseInt(secondPart);

            if (mainValue >= 0) {
                return new RangeValue(mainValue + addedValue, precision);
            } else {
                return new RangeValue(mainValue - addedValue, precision);
            }
        } else {
            return new RangeValue(Integer.parseInt(text));
        }
    }

}