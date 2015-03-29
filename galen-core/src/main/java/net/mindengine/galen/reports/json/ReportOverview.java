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
package net.mindengine.galen.reports.json;

import net.mindengine.galen.reports.GalenTestAggregatedInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ReportOverview {

    List<GalenTestAggregatedInfo> tests = new LinkedList<GalenTestAggregatedInfo>();

    public ReportOverview() {
    }

    public void add(GalenTestAggregatedInfo aggregatedInfo) {
        tests.add(aggregatedInfo);
    }

    public List<GalenTestAggregatedInfo> getTests() {
        return tests;
    }
}
