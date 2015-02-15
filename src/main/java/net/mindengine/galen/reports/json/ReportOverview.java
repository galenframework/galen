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
