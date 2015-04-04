package net.mindengine.galen.tests.integration;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.validation.ValidationResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;

public class ComponentFrameIntegrationTest {


    private WebDriver driver;

    @BeforeMethod
    public void createDriver() {
        driver = new FirefoxDriver();
        driver.get(toFileProtocol(getClass().getResource("/frame-page/main.html").getPath()));
    }


    @AfterMethod
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void shouldTest_componentFrameSpec_successfully() throws IOException {
        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/frame-page/passed.spec"), asList("desktop"));
        assertThat("Layout report should not have any errors",
                layoutReport.getValidationErrorResults(), is(emptyCollectionOf(ValidationResult.class)));
    }

    private String findSpec(String path) {
        return getClass().getResource(path).getPath();
    }

    private String toFileProtocol(String path) {
        return "file://" + path;
    }
}
