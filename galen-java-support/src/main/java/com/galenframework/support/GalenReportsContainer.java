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
package com.galenframework.support;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestReport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A singleton class which is used for storing Galen test reports
 */
public enum GalenReportsContainer {
    INSTANCE;

    private final List<GalenTestInfo> tests = Collections.synchronizedList(new ArrayList<GalenTestInfo>());

    /**
     * Returns a single instance of {@link GalenReportsContainer}
     * @return an instance of {@link GalenReportsContainer}
     */
    public static GalenReportsContainer get() {
        return INSTANCE;
    }

    public TestReport registerTest(Method method) {
        GalenTestInfo testInfo = GalenTestInfo.fromMethod(method);
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public TestReport registerTest(String name, List<String> groups) {
        GalenTestInfo testInfo = GalenTestInfo.fromString(name, groups);
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public TestReport registerTest(GalenTestInfo testInfo) {
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public List<GalenTestInfo> getAllTests() {
        return tests;
    }
}
