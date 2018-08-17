/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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


import com.galenframework.api.Galen;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.ValidationResult;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ComponentBasicIT {
    private WebDriver driver;

    @BeforeMethod
    public void createDriver() {
        driver = GalenUtils.createDriver(null, null, null);
        driver.manage().window().setSize(new Dimension(1024, 768));
    }

    @AfterMethod
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void componentSpec_shouldAllowToProvide_arguments_fromParentSpec() throws IOException {
        loadPage("/complex-page/index.html");
        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/complex-page/using-component-arguments.gspec"), asList("desktop"));

        assertThat("Amount of failures should be", layoutReport.errors(), is(1));

        assertThat(layoutReport.getValidationErrorResults().get(0).getChildValidationResults().get(0).getError().getMessages().get(0),
                is("\"message\" text is \"OMG!\" but should be \"Cool!\""));
    }


    /**
     * Comes from bug https://github.com/galenframework/galen/issues/353
     * @throws IOException
     */
    @Test
    public void componentSpec_shouldAllowToExecute_2ndLevel_componentSpec() throws IOException {
        loadPage("/2nd-level-component/index.html");
        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/2nd-level-component/quote-containers.gspec"), Collections.<String>emptyList());

        assertThat("Amount of failures should be", layoutReport.errors(), is(1));

        ValidationResult firstValidationError = layoutReport.getValidationErrorResults().get(0);
        assertThat(firstValidationError.getError().getMessages().get(0), is("Child component spec contains 1 errors"));

        ValidationResult secondValidationError = firstValidationError.getChildValidationResults().get(0);
        assertThat(secondValidationError.getError().getMessages().get(0), is("Child component spec contains 1 errors"));

        ValidationResult thirdValidationError = secondValidationError.getChildValidationResults().get(0);
        assertThat(thirdValidationError.getError().getMessages().get(0), is("\"name\" text is \"Buggy component\" but should start with \"Title\""));
    }

    private void loadPage(String url) {
        driver.get(toFileProtocol(getClass().getResource(url).getPath()));
    }


    @Test
    public void componentSpec_shouldWarn_ifThereAreWarnings_inChildren() throws IOException {
        loadPage("/complex-page/index.html");

        LayoutReport layoutReport = Galen.checkLayout(driver, findSpec("/complex-page/using-component-warnings.gspec"), asList("desktop"));

        assertThat("Amount of failures should be", layoutReport.errors(), is(0));
        assertThat("Amount of warnings should be", layoutReport.warnings(), is(1));

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
