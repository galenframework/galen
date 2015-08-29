package com.galenframework.reports.nodes;

public class ReportExtraFile extends ReportExtra<String> {

    public ReportExtraFile(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "file";
    }
}
