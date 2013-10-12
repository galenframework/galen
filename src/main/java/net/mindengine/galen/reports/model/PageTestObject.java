package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

public class PageTestObject {
    
    private String name;
    private List<PageTestSpec> specs = new LinkedList<PageTestSpec>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageTestSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PageTestSpec> specs) {
        this.specs = specs;
    }

}
