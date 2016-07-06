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
package com.galenframework.tests.specs;

import com.galenframework.specs.RangeValue;
import com.galenframework.specs.Range;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RangeTest {

    @Test(dataProvider = "holdsData")
    public void should_checkIfRange_holdsGivenValue(Range range, Double value, Boolean expected) {
        assertThat(range.holds(value), is(expected));
    }

    @Test
    public void should_getErrorMessageSuffix() {
        assertThat(Range.between(1, 5).getErrorMessageSuffix(), is("which is not in range of 1 to 5px"));
        assertThat(Range.between(1, 5).withPercentOf("other-object").getErrorMessageSuffix(), is("which is not in range of 1 to 5%"));
        assertThat(Range.exact(4).getErrorMessageSuffix(), is("instead of 4px"));
        assertThat(Range.greaterThan(4).getErrorMessageSuffix(), is("but it should be greater than 4px"));
        assertThat(Range.greaterThanOrEquals(4).getErrorMessageSuffix(), is("but it should be greater than or equal to 4px"));
        assertThat(Range.lessThan(4).getErrorMessageSuffix(), is("but it should be less than 4px"));
        assertThat(Range.lessThanOrEquals(4).getErrorMessageSuffix(), is("but it should be less than or equal to 4px"));
    }

    @DataProvider
    public Object[][] holdsData() {
        return new Object[][] {
                {Range.between(0, 10), 0.0, true},
                {Range.between(0, 10), -1.0, false},
                {Range.between(0, 10), 1.0, true},
                {Range.between(0, 10), 10.0, true},
                {Range.between(0, 10), 11.0, false},

                {Range.exact(0), 0.0, true},
                {Range.exact(-1), 0.0, false},
                {Range.exact(1), 0.0, false},
                {Range.exact(10), 0.0, false},

                {Range.lessThan(0), 0.0, false},
                {Range.lessThan(1), 0.0, true},
                {Range.lessThan(-1), 0.0, false},

                {Range.greaterThan(0), 0.0, false},
                {Range.greaterThan(-1), 0.0, true},
                {Range.greaterThan(1), 0.0, false},

                /* Precision checking */
                {Range.exact(new RangeValue(10, 0)), 10.0, true},
                {Range.exact(new RangeValue(10, 0)), 10.1, true},
                {Range.exact(new RangeValue(10, 0)), 10.99999, true},
                {Range.exact(new RangeValue(10, 0)), 9.99999, false},

                {Range.between(new RangeValue(10, 1), new RangeValue(20, 1)), 0.0, false},
                {Range.between(new RangeValue(10, 1), new RangeValue(20, 1)), 1.0, true},
                {Range.between(new RangeValue(10, 1), new RangeValue(20, 1)), 2.0, true},
                {Range.between(new RangeValue(10, 1), new RangeValue(20, 1)), 2.1, false},
                {Range.between(new RangeValue(10, 1), new RangeValue(20, 1)), 2.01, true},

                {Range.lessThan(new RangeValue(10, 1)), 0.0, true},
                {Range.lessThan(new RangeValue(10, 1)), 1.012, false},
                {Range.lessThan(new RangeValue(11, 1)), 1.012, true},
                {Range.lessThan(new RangeValue(10, 1)), 0.999, true},
                {Range.lessThanOrEquals(new RangeValue(10, 0)), 9.0, true},
                {Range.lessThanOrEquals(new RangeValue(10, 0)), 10.0, true},
                {Range.lessThanOrEquals(new RangeValue(10, 0)), 11.0, false},

                {Range.greaterThan(new RangeValue(10, 1)), 0.0, false},
                {Range.greaterThan(new RangeValue(10, 1)), 1.0, false},
                {Range.greaterThan(new RangeValue(10, 1)), 1.09999, false},
                {Range.greaterThan(new RangeValue(10, 1)), 1.1, true},
                {Range.greaterThan(new RangeValue(10, 1)), 1.1999, true},
                {Range.greaterThanOrEquals(new RangeValue(10, 0)), 10.0, true},
                {Range.greaterThanOrEquals(new RangeValue(10, 0)), 11.0, true},
                {Range.greaterThanOrEquals(new RangeValue(10, 0)), 9.0, false}
        };
    }
}
