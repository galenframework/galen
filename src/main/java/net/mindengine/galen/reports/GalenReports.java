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
package net.mindengine.galen.reports;

import java.util.LinkedList;
import java.util.List;

public class GalenReports {
    
    private static class GalenReportsHolder {
        private static final GalenReports INSTANCE = new GalenReports();
    }

    public static GalenReports get() {
        return GalenReportsHolder.INSTANCE;
    }

    private List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();
    
    private GalenReports() {
        
    }

    public static GalenReports createNew() {
        return new GalenReports();
    }

    public GalenTestInfo createTest(String testName) {
        GalenTestInfo test = new GalenTestInfo();
        test.setName(testName);
        
        getTests().add(test);
        return test;
    }

    public List<GalenTestInfo> getTests() {
        return tests;
    }

    public void setTests(List<GalenTestInfo> tests) {
        this.tests = tests;
    }

}
