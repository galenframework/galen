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
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.List;

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


        assertThat(layoutReport.getValidationErrorResults().size(), is(1));
        ValidationResult validationResult = layoutReport.getValidationErrorResults().get(0);

        assertThat(validationResult.getValidationObjects().size(), is(1));
        ValidationObject validationObject = validationResult.getValidationObjects().get(0);

        assertThat(validationObject.getArea().getLeft(), is(8));
        assertThat(validationObject.getArea().getTop(), allOf(greaterThan(30), lessThan(35)));
        assertThat(validationObject.getArea().getWidth(), allOf(greaterThan(300), lessThan(304)));
        assertThat(validationObject.getArea().getHeight(), allOf(greaterThan(150), lessThan(154)));

        assertThat(validationResult.getError(), is(new ValidationError(asList("Child component spec contains 1 errors"))));

        List<ValidationResult> childResults = validationResult.getChildValidationResults();
        assertThat(childResults.size(), is(1));

        assertThat("validation error should match regex",
                childResults.get(0).getError().getMessages().get(0).matches("\"frame-link\" height is (20|21|22|23|24|25)px instead of 40px"),
                is(true));
    }

    private String findSpec(String path) {
        return getClass().getResource(path).getPath();
    }

    private String toFileProtocol(String path) {
        return "file://" + path;
    }
}
