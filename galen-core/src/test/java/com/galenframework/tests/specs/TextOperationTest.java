/*******************************************************************************
* Copyright < YEAR > Ivan Shubin http://galenframework.com
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

import com.galenframework.validation.specs.TextOperation;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TextOperationTest {

    @Test
    public void shouldSupport_lowercase_operation() {
        assertThat(
                TextOperation.find("lowercase").apply("QWE"),
                is("qwe"));
    }

    @Test
    public void shouldSupport_uppercase_operation() {
        assertThat(
                TextOperation.find("uppercase").apply("qwE"),
                is("QWE"));
    }

    @Test
    public void shouldSupport_singleline_operation() {
        assertThat(
                TextOperation.find("singleline").apply("qwE\nqwe\ns"),
                is("qwE qwe s"));
    }
}
