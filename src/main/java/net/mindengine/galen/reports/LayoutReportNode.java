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

import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;

public class LayoutReportNode extends TestReportNode {

    private LayoutReport layoutReport;

    public LayoutReportNode(LayoutReport layoutReport, String name) {
        this.setLayoutReport(layoutReport);
        setName(name);
    }

    public LayoutReport getLayoutReport() {
        return layoutReport;
    }

    public void setLayoutReport(LayoutReport layoutReport) {
        this.layoutReport = layoutReport;
    }
    
    @Override
    public TestStatistic fetchStatistic(TestStatistic testStatistic) {
        if (layoutReport.getSections() != null) {
            for (LayoutSection section : layoutReport.getSections()) {
                if (section.getObjects() != null) {
                    for (LayoutObject object: section.getObjects()) {
                        fetchStatisticForObject(object, testStatistic);
                    }
                }
            }
        }
        
        return testStatistic;
    }

    private void fetchStatisticForObject(LayoutObject object, TestStatistic testStatistic) {
        if (object.getSpecs() != null) {
            for (LayoutSpec spec : object.getSpecs()) {

                /*
                 Checking if it was a component spec and if yes - than it will not take it into account
                 but rather will go into its child spec list
                  */
                if (spec.getSubObjects() != null && spec.getSubObjects().size() > 0) {
                    for (LayoutObject subObject : spec.getSubObjects()) {
                        fetchStatisticForObject(subObject, testStatistic);
                    }
                }
                else {
                    testStatistic.setTotal(testStatistic.getTotal() + 1);

                    if (spec.getFailed()) {
                        if (spec.isOnlyWarn()) {
                            testStatistic.setWarnings(testStatistic.getWarnings() + 1);
                        } else {
                            testStatistic.setErrors(testStatistic.getErrors() + 1);
                        }
                    } else testStatistic.setPassed(testStatistic.getPassed() + 1);
                }
            }
        }
    }

}
