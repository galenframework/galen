package com.galenframework.tests.integration;


import com.galenframework.api.Galen;
import com.galenframework.reports.model.LayoutReport;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ComponentBasicIT {
    private WebDriver driver;

    @BeforeMethod
    public void createDriver() {
        driver = new FirefoxDriver();
        driver.get(toFileProtocol(getClass().getResource("/complex-page/index.html").getPath()));
        driver.manage().window().setSize(new Dimension(1024, 768));
    }

    @AfterMethod
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void componentSpec_shouldAllowToProvide_arguments_fromParentSpec() throws IOException {
        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/complex-page/using-component-arguments.gspec"), asList("desktop"));

        assertThat("Amount of failures should be", layoutReport.errors(), is(1));

        assertThat(layoutReport.getValidationErrorResults().get(0).getChildValidationResults().get(0).getError().getMessages().get(0),
                is("\"message\" text is \"OMG!\" but should be \"Cool!\""));
    }

    private String findSpec(String path) {
        return getClass().getResource(path).getPath();
    }

    private String toFileProtocol(String path) {
        return "file://" + path;
    }
}
