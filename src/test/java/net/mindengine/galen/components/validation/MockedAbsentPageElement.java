package net.mindengine.galen.components.validation;

public class MockedAbsentPageElement extends MockedPageElement {

    public MockedAbsentPageElement(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    
    @Override
    public boolean isPresent() {
        return false;
    }
}
