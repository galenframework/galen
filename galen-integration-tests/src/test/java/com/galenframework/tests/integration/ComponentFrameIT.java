/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package com.galenframework.tests.integration;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import com.galenframework.api.Galen;
import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.util.DriverTestFactory;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationResult;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ComponentFrameIT {

    private WebDriver driver;

    @BeforeMethod
    public void createDriver() {
        driver = new FirefoxDriver();
        driver.get(toFileProtocol(getClass().getResource("/frame-page/main.html").getPath()));
        driver.manage().window().setSize(new Dimension(1024,768));
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
                .withObjects(asList(new ValidationObject(new Rect(8, 32, 302, 152), "frame")))
                .withError(new ValidationError(asList("Child component spec contains 1 errors")))
                .withChildValidationResults(asList(
                        new ValidationResult()
                                .withObjects(asList(new ValidationObject(new Rect(16, 89, 176, 20), "frame-link")))
                                .withError(new ValidationError(asList("\"frame-link\" height is 20px instead of 40px")))
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
