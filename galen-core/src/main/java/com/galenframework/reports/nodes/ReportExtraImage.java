package com.galenframework.reports.nodes;

public class ReportExtraImage extends ReportExtra<String> {
    public ReportExtraImage(String imagePath) {
        super(imagePath);
    }

    @Override
    public String getType() {
        return "image";
    }
}
