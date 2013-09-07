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
        this.setObjectName(objectName);
        this.setWebElement(webElement);
        this.setLocator(objectLocator);
    }

    @Override
    public Rect getArea() {
        Point location = getWebElement().getLocation();
        Dimension size = getWebElement().getSize();
        Rect rect = new Rect(location.getX(), location.getY(), size.getWidth(), size.getHeight());
        
        if (getLocator() != null && getLocator().getCorrections() != null) {
            return correctedRect(rect, getLocator().getCorrections());
        }
        else return rect;
    }

    private Rect correctedRect(Rect rect, Rect corrections) {
        return new Rect(rect.getLeft() + corrections.getLeft(),
                rect.getTop() + corrections.getTop(),
                rect.getWidth() + corrections.getWidth(),
                rect.getHeight() + corrections.getHeight());
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return getWebElement().isDisplayed();
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

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public String getText() {
        return getWebElement().getText().trim();
    }

}
