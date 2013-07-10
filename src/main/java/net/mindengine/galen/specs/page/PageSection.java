package net.mindengine.galen.specs.page;

import java.util.LinkedList;
import java.util.List;

public class PageSection {

    private List<String> tags;
    private List<ObjectSpecs> objects = new LinkedList<ObjectSpecs>();

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ObjectSpecs> getObjects() {
        return this.objects;
    }

    public void setObjects(List<ObjectSpecs> objects) {
        this.objects = objects;
    }

}
