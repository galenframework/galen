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
package net.mindengine.galen.tests.reports;

import net.mindengine.galen.reports.GalenTestInfo;
import org.testng.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.ConstructorOrMethod;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class GalenTestInfoTest {

    private long startDate;

    @BeforeMethod
    public void init() {
        startDate = new Date().getTime() - 10;
    }

    @Test
    public void shouldCreate_testInfo_fromString() {
        GalenTestInfo testInfo = new GalenTestInfo("Test 1");
        verifyTestInfo(testInfo, "Test 1", null);
    }

    @Test
    public void shouldCreate_testInfo_fromMethod() throws NoSuchMethodException {
        Method method = getClass().getMethod("shouldCreate_testInfo_fromMethod");
        GalenTestInfo testInfo = new GalenTestInfo(GalenTestInfoTest.class.getName(),method.getName());
        verifyTestInfo(testInfo, "shouldCreate_testInfo_fromMethod",GalenTestInfoTest.class.getName());
    }


    private void verifyTestInfo(GalenTestInfo testInfo, String name, String group) {
        assertThat(testInfo.getName(), is(name));
        if(group!=null){
            assertThat(testInfo.getGroup(), is(group));
        }
        assertThat(testInfo.getStartedAt().getTime(), is(greaterThan(startDate)));
        assertThat(testInfo.getEndedAt().getTime(), is(greaterThan(startDate)));
        assertThat(testInfo.getReport(), is(notNullValue()));
        assertThat(testInfo.getTest(), is(notNullValue()));
    }
}
