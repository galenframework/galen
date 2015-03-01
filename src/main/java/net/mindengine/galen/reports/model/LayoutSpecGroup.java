package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/28.
 */
public class LayoutSpecGroup {
    private String name;
    private List<LayoutSpec> specs;

    public List<LayoutSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<LayoutSpec> specs) {
        this.specs = specs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
