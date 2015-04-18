/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.specs;

import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.RangeValue;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RangeValueTest {


    @Test
    public void shouldImplement_equals_method() {
        assertThat(new RangeValue(123, 0).equals(new RangeValue(123, 0)), is(true));
        assertThat(new RangeValue(122, 0).equals(new RangeValue(123, 0)), is(false));
        assertThat(new RangeValue(123, 1).equals(new RangeValue(123, 0)), is(false));
    }

    @Test
    public void should_convertToString() {
        assertThat(new RangeValue(123.00199, 3).toString(), is("123.001"));
        assertThat(new RangeValue(123.0199, 2).toString(), is("123.01"));
        assertThat(new RangeValue(123.456, 3).toString(), is("123.456"));
        assertThat(new RangeValue(123.459, 2).toString(), is("123.45"));
        assertThat(new RangeValue(123.456, 1).toString(), is("123.4"));
        assertThat(new RangeValue(123.456, 0).toString(), is("123"));
        assertThat(new RangeValue(123).toString(), is("123"));
        assertThat(new RangeValue(0).toString(), is("0"));
        assertThat(new RangeValue(0.0, 1).toString(), is("0.0"));
    }

    @Test
    public void should_parseFromString_withPrecision() {
        assertThat(RangeValue.parseRangeValue("0"), is(new RangeValue(0, 0)));
        assertThat(RangeValue.parseRangeValue("0.0"), is(new RangeValue(0, 1)));
        assertThat(RangeValue.parseRangeValue("1.0"), is(new RangeValue(10, 1)));
        assertThat(RangeValue.parseRangeValue("-1.0"), is(new RangeValue(-10, 1)));
        assertThat(RangeValue.parseRangeValue("15.049"), is(new RangeValue(15049, 3)));
        assertThat(RangeValue.parseRangeValue("-15.049"), is(new RangeValue(-15049, 3)));
    }

    @Test
    public void should_returnAsInteger() {
        assertThat(new RangeValue(0, 0).asInt(), is(0));
        assertThat(new RangeValue(1, 1).asInt(), is(0));
        assertThat(new RangeValue(10, 1).asInt(), is(1));
        assertThat(new RangeValue(-19, 1).asInt(), is(-1));
        assertThat(new RangeValue(1000, 2).asInt(), is(10));
        assertThat(new RangeValue(-1000, 2).asInt(), is(-10));
    }

    @Test
    public void should_returnAsDouble() {
        assertThat(new RangeValue(0, 0).asDouble(), is(0.0));
        assertThat(new RangeValue(1, 1).asDouble(), is(0.1));
        assertThat(new RangeValue(10, 1).asDouble(), is(1.0));
        assertThat(new RangeValue(19, 1).asDouble(), is(1.9));
        assertThat(new RangeValue(-19, 1).asDouble(), is(-1.9));
        assertThat(new RangeValue(1000, 2).asDouble(), is(10.0));
        assertThat(new RangeValue(-1000, 2).asDouble(), is(-10.0));
        assertThat(new RangeValue(-1000, 5).asDouble(), is(-0.01));
    }

    @Test
    public void should_convertToString_negativeNumbers() {
        assertThat(new RangeValue(-123.00199, 3).toString(), is("-123.001"));
        assertThat(new RangeValue(-123.0199, 2).toString(), is("-123.01"));
        assertThat(new RangeValue(-123.456, 3).toString(), is("-123.456"));
        assertThat(new RangeValue(-123.459, 2).toString(), is("-123.45"));
        assertThat(new RangeValue(-123.456, 1).toString(), is("-123.4"));
        assertThat(new RangeValue(-123.456, 0).toString(), is("-123"));
        assertThat(new RangeValue(-123).toString(), is("-123"));
    }

    @Test
    public void equalsToMethod_comparesWithAnotherValue() {
        assertThat(new RangeValue(12345, 0).equalsTo(12345), is(true));
        assertThat(new RangeValue(12345, 0).equalsTo(12344), is(false));

        assertThat(new RangeValue(12345, 0).equalsTo(12345.12), is(true));
        assertThat(new RangeValue(12345, 0).equalsTo(12344.12), is(false));

        assertThat(new RangeValue(12300, 2).equalsTo(123), is(true));
        assertThat(new RangeValue(12345, 2).equalsTo(123), is(false));

        assertThat(new RangeValue(12345, 2).equalsTo(123.459999), is(true));
        assertThat(new RangeValue(12345, 2).equalsTo(123.449999), is(false));
    }


    @Test
    public void isLessThanOrEquals_comparesWithAnotherValue() {
        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12345), is(true));
        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12346), is(true));
        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12344), is(false));

        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12345.12), is(true));
        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12346.01), is(true));
        assertThat(new RangeValue(12345, 0).isLessThanOrEquals(12344.99), is(false));

        assertThat(new RangeValue(12300, 2).isLessThanOrEquals(123), is(true));
        assertThat(new RangeValue(12345, 2).isLessThanOrEquals(124), is(true));
        assertThat(new RangeValue(12345, 2).isLessThanOrEquals(123), is(false));

        assertThat(new RangeValue(12300, 2).isLessThanOrEquals(123.00123), is(true));
        assertThat(new RangeValue(12345, 2).isLessThanOrEquals(124.123), is(true));
        assertThat(new RangeValue(12345, 2).isLessThanOrEquals(122.99), is(false));
    }

    @Test
    public void isGreaterThanOrEquals_comparesWithAnotherValue() {
        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12345), is(true));
        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12344), is(true));
        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12346), is(false));

        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12345.12), is(true));
        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12344.01), is(true));
        assertThat(new RangeValue(12345, 0).isGreaterThanOrEquals(12346.99), is(false));

        assertThat(new RangeValue(12300, 2).isGreaterThanOrEquals(123), is(true));
        assertThat(new RangeValue(12345, 2).isGreaterThanOrEquals(123), is(true));
        assertThat(new RangeValue(12345, 2).isGreaterThanOrEquals(124), is(false));

        assertThat(new RangeValue(12300, 2).isGreaterThanOrEquals(123.00123), is(true));
        assertThat(new RangeValue(12345, 2).isGreaterThanOrEquals(123.01), is(true));
        assertThat(new RangeValue(12345, 2).isGreaterThanOrEquals(123.46), is(false));
    }

}
