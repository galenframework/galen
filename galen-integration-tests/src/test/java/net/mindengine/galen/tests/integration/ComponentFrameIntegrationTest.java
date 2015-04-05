package net.mindengine.galen.tests.integration;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationObject;
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

    @Test
    public void shouldTest_componentFrameSpec_andReportFailureProperly() throws IOException {
        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/frame-page/failed.spec"), asList("desktop"));

        assertThat(layoutReport.getValidationErrorResults(), contains(new ValidationResult()
                .withObjects(asList(new ValidationObject(new Rect(8, 68, 304, 154), "frame")))
                .withError(new ValidationError(asList("Child component spec contains 1 errors")))
                .withChildValidationResults(asList(
                        new ValidationResult()
                            .withObjects(asList(new ValidationObject(new Rect(16, 125, 184, 19), "frame-link")))
                            .withError(new ValidationError(asList("\"frame-link\" height is 19px instead of 40px")))
                ))
        ));
    }

    private String findSpec(String path) {
        return getClass().getResource(path).getPath();
    }

    private String toFileProtocol(String path) {
        return "file://" + path;
    }
}
