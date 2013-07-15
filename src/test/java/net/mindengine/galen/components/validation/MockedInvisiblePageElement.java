package net.mindengine.galen.components.validation;


public class MockedInvisiblePageElement extends MockedPageElement {

    public MockedInvisiblePageElement(int left, int top, int width, int height) {
        super(left, top, width, height);
    }
    
    @Override
    public boolean isVisible() {
        return false;
    }

}
