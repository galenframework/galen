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
package net.mindengine.galen.tests;

import java.util.List;

import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenBasicTestRunner;
import net.mindengine.galen.suite.GalenPageTest;

public class GalenBasicTest implements GalenTest {
    
    private String name;
    private List<GalenPageTest> pageTests;
    

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
        GalenBasicTestRunner suiteRunner = new GalenBasicTestRunner();
        suiteRunner.setSuiteListener(listener);
        suiteRunner.setValidationListener(listener);
        suiteRunner.runTest(report, this);
    }
    

}
