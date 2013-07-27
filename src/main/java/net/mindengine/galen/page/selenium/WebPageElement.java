package net.mindengine.galen.page.selenium;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.page.Locator;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class WebPageElement implements PageElement {

    private WebElement webElement;
    private Locator locator;
    private String objectName;
    

    public WebPageElement(String objectName, WebElement webElement, Locator objectLocator) {
        this.objectName = objectName;
        this.webElement = webElement;
        this.locator = objectLocator;
    }

    @Override
    public Rect getArea() {
        Point location = webElement.getLocation();
        Dimension size = webElement.getSize();
        Rect rect = new Rect(location.getX(), location.getY(), size.getWidth(), size.getHeight());
        return rect;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return webElement.isDisplayed();
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
        return getArea().getTop();
    }

}
