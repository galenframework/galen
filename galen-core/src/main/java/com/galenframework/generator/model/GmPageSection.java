package com.galenframework.generator.model;

import java.util.LinkedList;
import java.util.List;

public class GmPageSection {
    private String name;
    private List<GmSpecRule> rules = new LinkedList<>();
    private List<GmObjectSpecs> objectSpecs = new LinkedList<>();

    public GmPageSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GmSpecRule> getRules() {
        return rules;
    }

    public void setRules(List<GmSpecRule> rules) {
        this.rules = rules;
    }

    public List<GmObjectSpecs> getObjectSpecs() {
        return objectSpecs;
    }

    public void setObjectSpecs(List<GmObjectSpecs> objectSpecs) {
        this.objectSpecs = objectSpecs;
    }

    public boolean getHasContent() {
        return getHasRules()
            || (objectSpecs != null && !objectSpecs.isEmpty());
    }

    public boolean getHasRules() {
        return (rules != null && !rules.isEmpty());
    }
}
