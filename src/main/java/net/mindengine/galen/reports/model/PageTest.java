package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

public class PageTest {

    private String title = "";
    private List<PageTestObject> objects = new LinkedList<PageTestObject>();
    private List<Exception> globalErrors = new LinkedList<Exception>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PageTestObject> getObjects() {
        return objects;
    }

    public void setObjects(List<PageTestObject> objects) {
        this.objects = objects;
    }

    public List<Exception> getGlobalErrors() {
        return this.globalErrors;
    }
}
