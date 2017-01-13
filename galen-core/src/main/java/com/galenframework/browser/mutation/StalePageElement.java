package com.galenframework.browser.mutation;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;

public class StalePageElement extends PageElement {
    private final Rect area;
    private final boolean present;
    private final boolean visible;
    private final String text;

    public StalePageElement(PageElement original) {
        super();
        this.area = original.getArea();
        this.present = original.isPresent();
        this.visible = original.isVisible();
        this.text = original.getText();
    }

    @Override
    protected Rect calculateArea() {
        return area;
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public int getWidth() {
        return getArea().getWidth();
    }

    @Override
    public int getHeight() {
        return getArea().getHeight();
    }

    @Override
    public int getLeft() {
        return getArea().getLeft();
    }

    @Override
    public int getTop() {
        return getArea().getWidth();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getCssProperty(String cssPropertyName) {
        return "";
    }
}
