package com.galenframework.reports.nodes;

public class ReportExtraLink extends ReportExtra<String> {
    public ReportExtraLink(String link) {
        super(link);
    }

    @Override
    public String getType() {
        return "link";
    }
}
