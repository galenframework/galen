/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.api.mutation;

import com.galenframework.api.mutation.GalenMutate;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.components.mocks.driver.MockedDriver;
import com.galenframework.suite.actions.mutation.MutationReport;
import com.galenframework.validation.ValidationListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class GalenMutateTest {


    public static final ValidationListener NO_VALIDATION_LISTENER = null;

    @Test
    public void should_perform_mutation_testing() throws IOException {
        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/mutation-sample-page.json");

        MutationReport mutationReport = GalenMutate.checkAllMutations(new SeleniumBrowser(driver), "/specs/mutation.gspec",
            emptyList(), emptyList(), new Properties(), NO_VALIDATION_LISTENER);

        assertThat("amount of passed mutations", mutationReport.getTotalPassed(), is(56));
        assertThat("amount of failed mutations", mutationReport.getTotalFailed(), is(4));
        assertThat("All failed mutations", mutationReport.allFailedMutations(), contains(
            "container: increase height by 5px",
            "container: decrease height by 5px",
            "menu.item-3: increase width by 5px",
            "menu.item-3: decrease width by 5px"
        ));
    }
}
