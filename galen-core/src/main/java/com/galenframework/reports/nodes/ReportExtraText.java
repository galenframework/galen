package com.galenframework.reports.nodes;

public class ReportExtraText extends ReportExtra<String> {
    public ReportExtraText(String text) {
        super(text);
    }

    @Override
    public String getType() {
        return "text";
    }
}
