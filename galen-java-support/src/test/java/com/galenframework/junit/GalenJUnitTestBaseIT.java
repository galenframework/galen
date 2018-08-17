/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.junit;

import com.galenframework.utils.GalenUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.*;
import org.openqa.selenium.WebDriver;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class GalenJUnitTestBaseIT extends GalenJUnitTestBase {

    @Override
    public WebDriver createDriver() {
        return GalenUtils.createDriver(null, null, null);
    }

    @Test
    public void shouldInitDriver() {
        assertThat(getDriver(), notNullValue());
    }

    @Test
    public void shouldConcatenateClassAndMethodNameForTestName() {
        assertThat(getTestName(), is(equalTo(
                "com.galenframework.junit.GalenJUnitTestBaseIT#>shouldConcatenateClassAndMethodNameForTestName")));
    }

    @Parameters
    public static Iterable<String> devices() {
        return asList("dummy device");
    }

    @Parameter
    public Object device;
}
