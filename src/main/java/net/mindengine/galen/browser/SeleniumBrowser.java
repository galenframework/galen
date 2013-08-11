package net.mindengine.galen.browser;

import java.awt.Dimension;


import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.selenium.SeleniumPage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class SeleniumBrowser implements Browser {

    private WebDriver driver;

    public SeleniumBrowser(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public void changeWindowSize(Dimension windowSize) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(windowSize.width, windowSize.height));
    }

    @Override
    public void load(String url) {
        driver.get(url);
    }

    @Override
    public void executeJavascript(String javascript) {
        ((JavascriptExecutor)driver).executeScript(javascript);
    }

    @Override
    public Page getPage() {
        return new SeleniumPage(driver);
    }

}
