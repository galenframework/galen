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
package net.mindengine.galen.api;

import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class GalenReportsContainer {

    private static final GalenReportsContainer _instance = new GalenReportsContainer();
    private final List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();

    private GalenReportsContainer() {
    }

    public static final GalenReportsContainer get() {
        return _instance;
    }

    public TestReport registerTest(Method method) {
        GalenTestInfo testInfo = GalenTestInfo.fromMethod(method);
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public synchronized TestReport registerTest(final String name, final List<String> groups) {
        final GalenTestInfo testInfo = GalenTestInfo.fromString(name, groups);
        tests.add(testInfo);
        return testInfo.getReport();
    }

    public List<GalenTestInfo> getAllTests() {
        return tests;
    }
}
