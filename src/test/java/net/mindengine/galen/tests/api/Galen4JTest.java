/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.api;

import net.mindengine.galen.api.Galen4J;
import net.mindengine.galen.components.mocks.driver.MockedDriver;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.ValidationError;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class Galen4JTest {

    @Test
    public void checkLayout_shouldThrowException() throws IOException {
        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/galen4j-sample-page.json");


        LayoutReport layoutReport = Galen4J.checkLayout(driver, "/specs/galen4j/sample-spec-with-error.spec", asList("mobile"), null, new Properties(), null);

        assertThat(layoutReport.getValidationErrors(), contains(
                new ValidationError().withMessage("\"save-button\" is 10px left instead of 50px")
                        .withArea(new ErrorArea(new Rect(10, 10, 100, 50), "save-button"))
                        .withArea(new ErrorArea(new Rect(120, 10, 200, 50), "name-textfield")),
                new ValidationError().withMessage("\"save-button\" text is \"Save\" but should be \"Store\"")
                        .withArea(new ErrorArea(new Rect(10, 10, 100, 50), "save-button"))));
    }

    @Test
    public void checkLayout_shouldCreateReport() {

        throw new RuntimeException("not yet implemented");
    }
}
