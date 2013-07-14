package net.mindengine.galen.components.validation;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;

public class MockedPageElement implements PageElement {

    private Rect rect;

    public MockedPageElement(int left, int top, int width, int height) {
        this.rect = new Rect(left, top, width, height);
    }

    @Override
    public Rect getArea() {
        return rect;
    }

}
