package net.mindengine.galen.tests.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenPageActionInjectJavascriptTest {
    private static final String TEST_URL = "file://" + GalenPageActionCheckTest.class.getResource("/html/page1.html").getPath();
    
    @Test public void shouldInject_javascript() throws IOException {
        WebDriver driver = new FirefoxDriver();
        Browser browser = new SeleniumBrowser(driver);
        browser.load(TEST_URL);
        
        GalenPageActionInjectJavascript action = new GalenPageActionInjectJavascript("/scripts/to-inject-1.js"); 
        action.execute(browser, new GalenPageTest(), null);
        
        WebElement element = driver.findElement(By.xpath("//body/injected-tag"));
        
        assertThat("Inject tags text should be", element.getText(), is("Some injected content"));
        browser.quit();
    }
}
