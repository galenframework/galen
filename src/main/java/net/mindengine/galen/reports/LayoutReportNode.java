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
                
                testStatistic.setTotal(testStatistic.getTotal() + 1);
                
                if (spec.getFailed()) {
                    testStatistic.setErrors(testStatistic.getErrors() + 1);
                }
                else testStatistic.setPassed(testStatistic.getPassed() + 1);
                
                if (spec.getSubObjects() != null) {
                    for (LayoutObject subObject : spec.getSubObjects()) {
                        fetchStatisticForObject(subObject, testStatistic);
                    }
                }
            }
        }
    }

}
