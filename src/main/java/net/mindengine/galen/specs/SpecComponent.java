package net.mindengine.galen.specs;

import net.mindengine.galen.specs.reader.page.PageSpec;

public class SpecComponent extends Spec {

    private PageSpec pageSpec;

    public PageSpec getPageSpec() {
        return pageSpec;
    }

    public void setPageSpec(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }
}
