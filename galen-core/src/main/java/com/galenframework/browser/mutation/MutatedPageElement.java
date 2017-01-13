package com.galenframework.browser.mutation;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.suite.actions.mutation.AreaMutation;

public class MutatedPageElement extends PageElement {
    private final PageElement element;
    private final AreaMutation mutation;

    public MutatedPageElement(PageElement element, AreaMutation mutation) {
        this.element = element;
        this.mutation = mutation;
    }

    @Override
    protected Rect calculateArea() {
        return mutation.mutate(element.getArea());
    }

    @Override
    public boolean isPresent() {
        return element.isPresent();
    }

    @Override
    public boolean isVisible() {
        return element.isVisible();
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getLeft() {
        return 0;
    }

    @Override
    public int getTop() {
        return 0;
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return element.getCssProperty(cssPropertyName);
    }
}
