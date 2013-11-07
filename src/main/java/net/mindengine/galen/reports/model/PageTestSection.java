package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

public class PageTestSection {

    private String name;
    
    private List<PageTestObject> objects = new LinkedList<PageTestObject>();
    
    public List<PageTestObject> getObjects() {
        return objects;
    }

    public void setObjects(List<PageTestObject> objects) {
        this.objects = objects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
