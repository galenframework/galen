package net.mindengine.galen.page.selenium;

import net.mindengine.galen.page.AbsentPageElement;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.page.Locator;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class SeleniumPage implements Page {

    private WebDriver driver;

    public SeleniumPage(WebDriver driver) {
        this.driver = driver;
    }

    
    @Override
    public PageElement getObject(String objectName, Locator objectLocator) {
        By by = by(objectLocator);
        if (by != null) {
            try {
                return new WebPageElement(objectName, driver.findElement(by), objectLocator);
            }
            catch (NoSuchElementException e) {
                return new AbsentPageElement();
            }
        }
        return null;
    }

    private By by(Locator locator) {
        if ("xpath".equals(locator.getLocatorType())) {
            return By.xpath(locator.getLocatorValue());
        }
        else if ("id".equals(locator.getLocatorType())) {
            return By.id(locator.getLocatorValue());
        }
        else if ("css".equals(locator.getLocatorType())) {
            return By.cssSelector(locator.getLocatorValue());
        }
        else return null;
    }

}
