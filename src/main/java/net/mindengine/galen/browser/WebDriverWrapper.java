package net.mindengine.galen.browser;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

public class WebDriverWrapper {

    private WebDriver driver;

    public WebDriverWrapper(WebDriver driver) {
        this.driver = driver;
    }
    
    
    public void close() {
        driver.close();
    }

    
    public WebElementWrapper findElement(By by) {
        return new WebElementWrapper(driver.findElement(by));
    }

    
    public WebElementWrapper[] findElements(By by) {
        List<WebElement> elementsList = driver.findElements(by);
        return convertToArray(elementsList);
    }


    public static WebElementWrapper[] convertToArray(List<WebElement> elementsList) {
        WebElementWrapper[] elements = new WebElementWrapper[elementsList.size()];
        
        int i=0;
        for (WebElement element : elementsList) {
            elements[i] = new WebElementWrapper(element);
            i++;
        }
        return elements;
    }
    
    
    

    
    public void get(String url) {
        driver.get(url);
    }

    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    
    public String getPageSource() {
        return driver.getPageSource();
    }

    
    public String getTitle() {
        return driver.getTitle();
    }

    
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    
    public String[] getWindowHandles() {
        Set<String> handlesSet = driver.getWindowHandles();
        if (handlesSet != null) {
            return handlesSet.toArray(new String[]{});
        }
        else return new String[]{};
    }

    
    public Options manage() {
        return driver.manage();
    }

    
    public Navigation navigate() {
        return driver.navigate();
    }

    
    public void quit() {
        driver.quit();
    }

    
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

}
