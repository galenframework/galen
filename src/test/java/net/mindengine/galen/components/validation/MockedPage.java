package net.mindengine.galen.components.validation;

import java.util.HashMap;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;

public class MockedPage implements Page {

    private HashMap<String, PageElement> elements;

    public MockedPage(HashMap<String, PageElement> elements) {
        this.elements = elements;
    }

    @Override
    public PageElement getObject(String objectName) {
        return elements.get(objectName);
    }

    
}
