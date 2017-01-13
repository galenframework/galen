package com.galenframework.suite.actions.mutation;

public class PageElementMutation {
    private String elementName;
    private AreaMutation areaMutation;

    public PageElementMutation(String elementName, AreaMutation areaMutation) {
        this.elementName = elementName;
        this.areaMutation = areaMutation;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public AreaMutation getAreaMutation() {
        return areaMutation;
    }

    public void setAreaMutation(AreaMutation areaMutation) {
        this.areaMutation = areaMutation;
    }
}
