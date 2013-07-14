package net.mindengine.galen.page;

import static java.lang.String.format;

public class Point {

    private int left;
    private int top;

    public Point(int left, int top) {
        this.setLeft(left);
        this.setTop(top);
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    
    @Override
    public String toString() {
        return format("Point(left: %d, top: %d)", left, top);
    }
}
