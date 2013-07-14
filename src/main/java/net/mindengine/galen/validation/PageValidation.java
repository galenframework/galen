package net.mindengine.galen.validation;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.reader.page.PageSpec;

public class PageValidation {

    private Page page;
    private PageSpec pageSpec;

    public PageValidation(Page page, PageSpec pageSpec) {
        this.setPage(page);
        this.setPageSpec(pageSpec);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ValidationError check(String objectName, Spec spec) {
        SpecValidation specValidation = ValidationFactory.getValidation(spec, this);
        return specValidation.check(objectName, spec);
    }

    public PageSpec getPageSpec() {
        return pageSpec;
    }

    public void setPageSpec(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    public Rect getObjectArea(String objectName) {
        PageElement pageElement = page.getObject(objectName);
        if (pageElement != null) {
            return pageElement.getArea();
        }
        else return null;
    }

}
