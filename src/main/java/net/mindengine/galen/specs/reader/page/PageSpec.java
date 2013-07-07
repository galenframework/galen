package net.mindengine.galen.specs.reader.page;

import java.util.List;
import java.util.Map;

import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.page.PageSection;


public class PageSpec {

    private Map<String, Locator> objects;
    private List<PageSection> sections;

    public Map<String, Locator> getObjects() {
        return this.objects;
    }

    public void setObjects(Map<String, Locator> objects) {
        this.objects = objects;
    }

    public List<PageSection> getSections() {
        return this.sections;
    }

    public void setSections(List<PageSection> sections) {
        this.sections = sections;
    }

}
