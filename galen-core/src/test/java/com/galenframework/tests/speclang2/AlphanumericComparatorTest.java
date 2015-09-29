/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.speclang2;

import com.galenframework.speclang2.AlphanumericComparator;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AlphanumericComparatorTest {


    @Test
    public void shouldSortProperly() {
        List<String> list = asList(
                "abc 123 edf2",
                "abc 123 edf",
                "abc 2 edf",
                "abc 13 edf",
                "abc 12 edf",
                "abd 2 edf",
                "abb 2 edf"
                );

        Collections.sort(list, new AlphanumericComparator());

        assertThat(list, is(asList(
                "abb 2 edf",
                "abc 2 edf",
                "abc 12 edf",
                "abc 13 edf",
                "abc 123 edf",
                "abc 123 edf2",
                "abd 2 edf"
        )));
    }
}
