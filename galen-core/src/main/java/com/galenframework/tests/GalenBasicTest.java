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
package com.galenframework.tests;

import java.util.List;

import com.galenframework.runner.CompleteListener;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.reports.TestReport;
import com.galenframework.runner.CompleteListener;
import com.galenframework.runner.GalenBasicTestRunner;
import com.galenframework.suite.GalenPageTest;

public class GalenBasicTest implements GalenTest {
    
    private String name;
    private List<GalenPageTest> pageTests;
    private List<String> groups;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GalenPageTest> getPageTests() {
        return pageTests;
    }

    public void setPageTests(List<GalenPageTest> pageTests) {
        this.pageTests = pageTests;
    }

    @Override
    public void execute(TestReport report, CompleteListener listener) throws Exception {
        GalenBasicTestRunner testRunner = new GalenBasicTestRunner();
        testRunner.setSuiteListener(listener);
        testRunner.setValidationListener(listener);
        testRunner.runTest(report, this);
    }

    @Override
    public List<String> getGroups() {
        return this.groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }


}
