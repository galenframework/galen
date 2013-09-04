package net.mindengine.galen.browser;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class WebElementWrapper {

    private WebElement webElement;

    public WebElementWrapper(WebElement webElement) {
        this.webElement = webElement;
    }

    public void click() {
        webElement.click();
    }

    public void submit() {
        webElement.submit();
    }

    public void sendKeys(String keys) {
        webElement.sendKeys(keys);
    }

    
    public void clear() {
        webElement.clear();
    }

    public String getTagName() {
        return webElement.getTagName();
    }

    public String getAttribute(String name) {
        return webElement.getAttribute(name);
    }

    public boolean isSelected() {
        return webElement.isSelected();
    }

    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    public String getText() {
        return webElement.getText();
    }

    public WebElementWrapper[] findElements(By by) {
        return WebDriverWrapper.convertToArray(webElement.findElements(by));
    }

    public WebElementWrapper findElement(By by) {
        return new WebElementWrapper(webElement.findElement(by));
    }

    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    public Point getLocation() {
        return webElement.getLocation();
    }

    public Dimension getSize() {
        return webElement.getSize();
    }

    public String getCssValue(String propertyName) {
        return webElement.getCssValue(propertyName);
    }

}
