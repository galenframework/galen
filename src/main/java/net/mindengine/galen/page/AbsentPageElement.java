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

}
