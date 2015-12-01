package com.galenframework.tests.integration;

import com.galenframework.testng.GalenTestNgTestBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.util.Arrays.asList;

public class TempTestIT extends GalenTestNgTestBase {
    @Override
    public WebDriver createDriver(Object[] args) {
        return new FirefoxDriver();
    }


    @Test
    public void shouldCheckLayout() throws IOException {
        load("http://testapp.galenframework.com/bad-version/", 1024, 768);

        checkLayout("galen-sample-js-project/specs/welcomePage.gspec", asList("desktop"));
    }


}
