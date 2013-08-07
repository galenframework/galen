package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.runner.GalenTestRunner;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class GalenRunnerTest {

    private static final String TEST_URL = "file://" + GalenRunnerTest.class.getResource("/html/page1.html").getPath();


    @Test public void runsTestSuccessfully_andInvokesListener() throws IOException {
        
        TestValidationListener validationListener = new TestValidationListener();
        
        GalenTestRunner runner = new GalenTestRunner()
            .withUrl(TEST_URL)
            .withScreenSize(new Dimension(400, 800))
            .withIncludedTags(asList("mobile"))
            .withSpec(new PageSpecReader().read(GalenRunnerTest.class.getResourceAsStream("/html/page.spec")))
            .withValidationListener(validationListener);
    
        List<ValidationError> errors = runner.run();
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
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
    
    
    @Test public void runsTestSuccessfully_inPredefinedBrowser() throws IOException {
        TestValidationListener validationListener = new TestValidationListener();
        
        WebDriver driver = new FirefoxDriver();
        
        String url = "file://" + getClass().getResource("/html/page1.html").getPath();
        GalenTestRunner runner = new GalenTestRunner()
            .withUrl(TEST_URL)
            .withScreenSize(new Dimension(400, 800))
            .withIncludedTags(asList("mobile"))
            .withSpec(new PageSpecReader().read(GalenRunnerTest.class.getResourceAsStream("/html/page.spec")))
            .withValidationListener(validationListener);
    
        List<ValidationError> errors = runner.run(driver);
        
        String currentUrl = driver.getCurrentUrl();
        driver.quit();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
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
    
    /*
    @Test
    public void runsTestSuccessfully_andGenerates_htmlReport() {
        String htmlReportPath = String.format("/tmp/html-report-output-%s.html", UUID.randomUUID().toString());
        new GalenRunner().run(new GalenArguments()
                    .withAction("run")
                    .withUrl("file://" + getClass().getResource("/html/page1.html").getPath())
                    .withIncludedTags("mobile")
                    .withSpec(getClass().getResource("/html/page.spec").getPath())
                    .withScreenSize(new Dimension(400, 800))
                    .withHtmlReport(htmlReportPath));
        
        
        
        assertThat("Html report should be generated", new File(htmlReportPath).exists(), is(true));
    }
    */
    
    
    //TODO javascript injection tests
    //TODO test tags exclusion
}
