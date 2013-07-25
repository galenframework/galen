package net.mindengine.galen.page.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.page.Locator;

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
        System.out.println("   rect for " + objectName + " " + rect.toString());
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

}
