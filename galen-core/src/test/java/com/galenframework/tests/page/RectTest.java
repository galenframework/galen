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
package com.galenframework.tests.page;

import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RectTest {


    @Test(dataProvider = "provider_calculatePointOffsetDistance")
    public void shouldPass_calculatePointOffsetDistance(Point point, int expectedDistance) {
        Rect rect = new Rect(10, 10, 30, 20);
        assertThat(rect.calculatePointOffsetDistance(point), is(expectedDistance));
    }

    @DataProvider
    public Object[][] provider_calculatePointOffsetDistance() {
        return new Object[][]{
                {point(10, 10), 0},
                {point(10, 15), 0},
                {point(15, 10), 0},
                {point(40, 28), 0},
                {point(38, 30), 0},

                {point(11, 11), -1},
                {point(11, 12), -1},
                {point(15, 15), -5},
                {point(15, 20), -5},

                {point(9, 10), 1},
                {point(10, 9), 1},
                {point(9, 9), 1},
                {point(9, 8), 2},

                {point(41, 10), 1},
                {point(42, 11), 2},

                {point(20, 8), 2},
                {point(20, 32), 2},

                {point(5, 5), 5},
                {point(8, 3), 7},
                {point(10, 3), 7},
                {point(25, 3), 7},
                {point(40, 3), 7},
                {point(43, 3), 7},
                {point(45, 5), 5},
                {point(48, 8), 8},
                {point(48, 10), 8},
                {point(48, 20), 8},
                {point(48, 30), 8},
                {point(48, 33), 8},
                {point(45, 35), 5},
                {point(43, 38), 8},
                {point(40, 38), 8},
                {point(25, 38), 8},
                {point(10, 38), 8},
                {point(8, 38), 8},
                {point(5, 35), 5},
                {point(2, 33), 8},
                {point(2, 30), 8},
                {point(2, 20), 8},
                {point(2, 8), 8}
        };
    }

    private Point point(int left, int top) {
        return new Point(left, top);
    }

    private Rect rect(int left, int top, int width, int height) {
        return new Rect(left, top, width, height);
    }
}
