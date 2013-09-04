package net.mindengine.galen.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenPageActionRunJavascriptTest {
    private static final String TEST_URL = "file://" + GalenPageActionCheckTest.class.getResource("/html/page-for-js-check.html").getPath();
    
    @Test public void shouldRun_javascriptFile_andPerformActions_onBrowser() throws Exception {
        WebDriver driver = new FirefoxDriver();
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        
        WebElement element = driver.findElement(By.id("search-query"));
        assertThat("Search input should not contain any text yet", element.getAttribute("value"), is(""));
        
        GalenPageActionRunJavascript action = new GalenPageActionRunJavascript("/scripts/to-run-1.js");
        action.setJsonArguments("{prefix: 'This was'}");
        
        action.execute(browser, new GalenPageTest(), null);
        
        assertThat("Search input should contain text", element.getAttribute("value"), is("This was typed by a selenium from javascript"));
        browser.quit();
    }
}
