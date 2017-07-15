package com.galenframework.generator.model;

import java.util.LinkedList;
import java.util.List;

public class GmObjectSpecs {
    private String objectName;
    private List<GmSpec> specs = new LinkedList<>();

    public GmObjectSpecs(String objectName) {
        this.objectName = objectName;
    }

    public List<GmSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<GmSpec> specs) {
        this.specs = specs;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
