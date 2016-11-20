/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.GalenMain;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.reports.TestStatistic;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.galenframework.tests.integration.RegexBuilder.regex;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class GalenFullJsProjectIT {
    public static final String PATH_TO_TEST_PROJECT = GalenFullJsProjectIT.class.getResource("/galen-sample-js-project").getFile();
    public static final String PATH_TO_TEST_WEBSITE = "file://" + GalenFullJsProjectIT.class.getResource("/sample-test-website/index.html").getFile();


    @Test
    public void shouldExecute_completeJsSampleProject() throws Exception {
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_USE_FAIL_EXIT_CODE, "false");
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_BROWSER_VIEWPORT_ADJUSTSIZE, "true");
        System.setProperty("sample.test.website", PATH_TO_TEST_WEBSITE);
        System.setProperty("sample.project.path", PATH_TO_TEST_PROJECT);

        String jsonReportPath = Files.createTempDir().getAbsolutePath() + "/json-report";
        String htmlReportPath = Files.createTempDir().getAbsolutePath() + "/html-report";

        System.out.println(htmlReportPath);

        new GalenMain().execute("test", PATH_TO_TEST_PROJECT + "/tests/", "--htmlreport", htmlReportPath, "--jsonreport", jsonReportPath);

        assertReports(htmlReportPath, jsonReportPath);
    }

    private void assertReports(String htmlReportPath, String jsonReportPath) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonTree = mapper.readTree(FileUtils.readFileToString(new File(jsonReportPath + "/report.json")));

            assertMap(toMap(jsonTree), new HashMap<String, TestStatistic>() {{
                        put("Add note page on desktop emulation device", new TestStatistic(74, 0, 0, 74));
                        put("Add note page on mobile emulation device", new TestStatistic(73, 0, 0, 73));
                        put("Add note page on tablet emulation device", new TestStatistic(74, 0, 0, 74));
                        put("Login page on desktop emulation device", new TestStatistic(80, 0, 0, 80));
                        put("Login page on mobile emulation device", new TestStatistic(84, 0, 0, 84));
                        put("Login page on tablet emulation device", new TestStatistic(79, 0, 0, 79));
                        put("My notes page on desktop emulation device", new TestStatistic(81, 0, 0, 81));
                        put("My notes page on mobile emulation device", new TestStatistic(83, 0, 0, 83));
                        put("My notes page on tablet emulation device", new TestStatistic(81, 0, 0, 81));
                        put("Welcome page on desktop emulation device", new TestStatistic(70, 1, 0, 71));
                        put("Welcome page on mobile emulation device", new TestStatistic(68, 0, 0, 68));
                        put("Welcome page on tablet emulation device", new TestStatistic(70, 1, 0, 71));

                    }}
            );


            List<String> errorMessages = collectAllErrorMessages(jsonTree, jsonReportPath);
            assertErrorMessages(errorMessages, asList(
                regex().exact("\"login_button\" width is ").digits(2).exact("px instead of 20px").toString(),
                regex().exact("\"login_button\" width is ").digits(2).exact("px instead of 20px").toString()
            ));
        } catch (Exception ex) {
            throw new RuntimeException("Report validation failed:\n" +
                    "Html Report: " + htmlReportPath + "/report.html\n" +
                    "Json Report: " + jsonReportPath + "/report.json", ex);
        }
    }

    private void assertMap(Map<String, TestStatistic> realMap,  Map<String, TestStatistic> expectedMap) {
        assertEquals(realMap.size(), expectedMap.size());

        for (Map.Entry<String, TestStatistic> expected : expectedMap.entrySet()) {
            assertTrue(realMap.containsKey(expected.getKey()), "Should contain: " + expected.getKey());
            assertEquals(realMap.get(expected.getKey()), expected.getValue(),
                    "Should have same value for key:" + expected.getKey() + " \nas: " + expected.getValue()
                            + ", but got: " + realMap.get(expected.getKey()));
        }
    }

    private void assertErrorMessages(List<String> errorMessages, List<String> expected) {
        assertEquals(errorMessages.size(), expected.size());

        Iterator<String> expectedIterator = expected.iterator();

        int number = 0;
        for (String errorMessage : errorMessages) {
            number ++;
            String pattern = expectedIterator.next();
            Assert.assertTrue(errorMessage.matches(pattern), "Error message #" + number + ": " + errorMessage + ",\n should match pattern: " + pattern);
        }
    }

    private List<String> collectAllErrorMessages(JsonNode rootNode, String jsonReportPath) throws IOException {
        JsonNode testsNode = rootNode.get("tests");
        Iterator<JsonNode> it = testsNode.iterator();

        List<String> errorMessages = new LinkedList<>();

        while(it.hasNext()) {
            String testId = it.next().get("testId").asText();
            errorMessages.addAll(collectAllErrorMessagesFromIndividualTestReport(testId, jsonReportPath));
        }

        return errorMessages;
    }

    private List<String> collectAllErrorMessagesFromIndividualTestReport(String testId, String jsonReportPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode testReportNode = mapper.readTree(FileUtils.readFileToString(new File(jsonReportPath + "/" + testId + ".json")));
        return collectAllErrorMessagesFrom(testReportNode.get("report").get("nodes"));
    }

    private List<String> collectAllErrorMessagesFrom(JsonNode jsonNode) {
        List<String> list = new LinkedList<>();

        if (jsonNode.isArray()) {
            for (JsonNode aJsonNode : jsonNode) {
                list.addAll(collectAllErrorMessagesFrom(aJsonNode));
            }
        } else {
            Iterator<String> fieldsIterator = jsonNode.fieldNames();
            while(fieldsIterator.hasNext()) {
                String fieldName = fieldsIterator.next();
                if ("errors".equals(fieldName)) {
                    JsonNode errorsNode = jsonNode.get(fieldName);
                    if (errorsNode.isArray()) {
                        for (JsonNode errorNode : errorsNode) {
                            list.add(errorNode.asText());
                        }
                    }
                } else {
                    list.addAll(collectAllErrorMessagesFrom(jsonNode.get(fieldName)));
                }
            }
        }
        return list;
    }


    private HashMap<String, TestStatistic> toMap(JsonNode rootNode) {
        JsonNode testsNode = rootNode.get("tests");
        Iterator<JsonNode> it = testsNode.iterator();

        HashMap<String, TestStatistic> map = new HashMap<>();

        while(it.hasNext()) {
            JsonNode testNode = it.next();
            JsonNode statisticNode = testNode.get("statistic");
            map.put(testNode.get("name").asText(), new TestStatistic(
                    statisticNode.get("passed").asInt(),
                    statisticNode.get("errors").asInt(),
                    statisticNode.get("warnings").asInt(),
                    statisticNode.get("total").asInt()
            ));
        }

        return map;
    }

}
