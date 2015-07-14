/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import net.mindengine.galen.reports.json.ReportOverview;
import net.mindengine.galen.util.GalenBaseTestRunner;

import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class SampleTestWebsiteIT extends GalenBaseTestRunner {
    ObjectMapper mapper = new ObjectMapper(){{
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }};

    @Test(dataProvider = "devices")
    public void welcomePage_shouldLookGood_onDevice(TestDevice device) throws Exception {
        File reportFolder = Files.createTempDir();
        verifyPage("/sample-test-website/index.html", device, "/specs/welcomePage.spec", reportFolder);


        assertThat(asList(reportFolder.list()), hasItems(
                "report.json",
                "report.html"
        ));

        JsonNode report = mapper.readTree(new FileInputStream(reportFolder.getAbsolutePath() + "/report.json"));
        JsonNode tests = report.get("tests");
        assertThat("Amount of tests", tests.size(), is(1));

        JsonNode test = tests.get(0);
        JsonNode statistics = test.get("statistic");

        assertThat("Amount of errors should be", statistics.get("errors").asInt(), is(0));
        assertThat("Amount of warnings should be", statistics.get("warnings").asInt(), is(0));
        assertThat("Amount of passed should be", statistics.get("passed").asInt(), greaterThan(30));
        assertThat("Amount of total should be", statistics.get("total").asInt(), greaterThan(30));
    }
}
