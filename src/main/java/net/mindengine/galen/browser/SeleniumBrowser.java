package net.mindengine.galen.browser;

import java.awt.Dimension;
import java.io.File;


import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.selenium.SeleniumPage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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

    @Override
    public String getUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public Dimension getScreenSize() {
        org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
        return new Dimension(windowSize.getWidth(), windowSize.getHeight());
    }

    @Override
    public String createScreenshot() {
        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        return file.getAbsolutePath();
    }

}
