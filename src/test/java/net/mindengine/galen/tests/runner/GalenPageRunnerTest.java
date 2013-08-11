package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenPageRunnerTest {

    private static final String TEST_URL = "file://" + GalenPageRunnerTest.class.getResource("/html/page1.html").getPath();


    @Test public void runsTestSuccessfully_inPredefinedBrowser() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new FirefoxDriver();
        
        String url = "file://" + getClass().getResource("/html/page1.html").getPath();
        GalenPageRunner runner = new GalenPageRunner()
            .withUrl(TEST_URL)
            .withScreenSize(new Dimension(400, 800))
            .withIncludedTags(asList("mobile"))
            .withSpec(new PageSpecReader().read(GalenPageRunnerTest.class.getResourceAsStream("/html/page.spec")))
            .withValidationListener(validationListener);
    
        List<ValidationError> errors = runner.run(new SeleniumBrowser(driver));
        
        String currentUrl = driver.getCurrentUrl();
        driver.quit();
        
        assertThat("Invokations should be", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 170px</msg></e>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(1));
        
        assertThat("Driver should have test page open", currentUrl, is(url));
    }
    
    @Test public void runsTestSuccessfully_withInjectedJavascript() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        String javascript = "$('body').append('<div>injected by javascript</div>');";
        GalenPageRunner runner = new GalenPageRunner()
            .withUrl(TEST_URL)
            .withScreenSize(new Dimension(400, 800))
            .withIncludedTags(asList("mobile"))
            .withJavascript(javascript)
            .withSpec(new PageSpecReader().read(GalenPageRunnerTest.class.getResourceAsStream("/html/page.spec")))
            .withValidationListener(validationListener);
    
        WebDriver driver = new FirefoxDriver();
        
        List<ValidationError> errors = runner.run(new SeleniumBrowser(driver));
        String pageSource = driver.getPageSource();
        driver.quit();
        
        assertThat("Invokations should be", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 170px</msg></e>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(1));
        
        assertThat("Javascript should be run", pageSource, containsString("<div>injected by javascript</div>"));
    }
    
    @Test public void runsTestSuccessfully_andExlcudesSpecifiedTags() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new FirefoxDriver();
        
        GalenPageRunner runner = new GalenPageRunner()
            .withUrl(TEST_URL)
            .withScreenSize(new Dimension(400, 800))
            .withIncludedTags(asList("mobile"))
            .withExcludedTags(asList("debug"))
            .withSpec(new PageSpecReader().read(GalenPageRunnerTest.class.getResourceAsStream("/html/page-exclusion.spec")))
            .withValidationListener(validationListener);
    
        List<ValidationError> errors = runner.run(new SeleniumBrowser(driver));
        
        driver.quit();
        
        assertThat("Invokations should be", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 170px</msg></e>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(1));
    }
    
}
