package net.mindengine.galen.parser;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;

/**
 * Created by ishubin on 2014/11/02.
 */
public class JsPageElement {
    private PageElement pageElement;

    public JsPageElement(PageElement pageElement) {
        this.pageElement = pageElement;
    }

    public PageElement getPageElement() {
        return pageElement;
    }

    public int left() {
        return pageElement.getArea().getLeft();
    }
    public int top() {
        return pageElement.getArea().getTop();
    }
    public int right() {
        Rect area = pageElement.getArea();
        return area.getLeft() + area.getWidth();
    }
    public int bottom() {
        Rect area = pageElement.getArea();
        return area.getTop() + area.getHeight();
    }
}
