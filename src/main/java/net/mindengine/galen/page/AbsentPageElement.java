package net.mindengine.galen.page;

public class AbsentPageElement implements PageElement {

    @Override
    public Rect getArea() {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

}
