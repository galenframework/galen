package com.galenframework.tests.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.GalenMain;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.reports.TestStatistic;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.galenframework.tests.integration.RegexBuilder.regex;
import static java.util.Arrays.asList;
import static org.cthul.matchers.CthulMatchers.matchesPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class GalenFullJsProjectIT {
    public static final String PATH_TO_TEST_PROJECT = System.getProperty("user.dir")
            + "/galen-integration-tests/src/test/resources/galen-sample-js-project/tests/";

    @Test
    public void shouldExecute_completeJsSampleProject() throws Exception {
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_USE_FAIL_EXIT_CODE, "false");
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_BROWSER_VIEWPORT_ADJUSTSIZE, "true");

        String jsonReportPath = Files.createTempDir().getAbsolutePath() + "/json-report";
        String htmlReportPath = Files.createTempDir().getAbsolutePath() + "/html-report";

        new GalenMain().execute("test", PATH_TO_TEST_PROJECT, "--htmlreport", htmlReportPath, "--jsonreport", jsonReportPath);

        assertReports(htmlReportPath, jsonReportPath);
    }

    private void assertReports(String htmlReportPath, String jsonReportPath) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonTree = mapper.readTree(FileUtils.readFileToString(new File(jsonReportPath + "/report.json")));

            assertThat(toMap(jsonTree), allOf(
                    hasEntry("Welcome page long words test on mobile device", new TestStatistic(32, 10, 0, 42)),
                    hasEntry("Welcome page long words test on tablet device", new TestStatistic(43, 10, 0, 53)),
                    hasEntry("Welcome page long words test on desktop device", new TestStatistic(47, 9, 0, 56)),
                    hasEntry("Add note page on desktop device", new TestStatistic(60, 0, 0, 60)),
                    hasEntry("Add note page on mobile device", new TestStatistic(43, 0, 0, 43)),
                    hasEntry("Add note page on tablet device", new TestStatistic(57, 0, 0, 57)),
                    hasEntry("Login page on desktop device", new TestStatistic(65, 0, 0, 65)),
                    hasEntry("Login page on mobile device", new TestStatistic(50, 0, 0, 50)),
                    hasEntry("Login page on tablet device", new TestStatistic(62, 0, 0, 62)),
                    hasEntry("Menu Highlight on desktop device", new TestStatistic(3, 0, 0, 3)),
                    hasEntry("My notes page on desktop device", new TestStatistic(63, 0, 0, 63)),
                    hasEntry("My notes page on mobile device", new TestStatistic(47, 0, 0, 47)),
                    hasEntry("My notes page on tablet device", new TestStatistic(60, 0, 0, 60)),
                    hasEntry("Welcome page on desktop device", new TestStatistic(49, 0, 0, 49)),
                    hasEntry("Welcome page on mobile device", new TestStatistic(35, 0, 0, 35)),
                    hasEntry("Welcome page on tablet device", new TestStatistic(46, 0, 0, 46))
            ));


            List<String> errorMessages = collectAllErrorMessages(jsonTree, jsonReportPath);
            assertErrorMessages(errorMessages, asList(
                    regex().exact("\"header\" is not centered horizontally inside \"screen\". Offset is ").digits(2).exact("px").toString(),
                    regex().exact("\"header.text\" is not completely inside").toString(),
                    regex().exact("\"header.text\" text is \"Freundschaftsbezeigungen\" but should be \"Sample Website\"").toString(),
                    regex().exact("\"menu\" is not centered horizontally inside \"screen\". Offset is ").digits(2).exact("px").toString(),
                    regex().exact("\"menu.item-1\" width is ").digits().exact("% [").digits(3).exact("px] which is not in range of 48 to 50% [250 to 260px]").toString(),
                    regex().exact("\"menu.item-2\" width is ").digits().exact("% [").digits(3).exact("px] which is not in range of 48 to 50% [250 to 260px]").toString(),
                    regex().exact("\"menu.item-3\" width is ").digits().exact("% [").digits(3).exact("px] which is not in range of 48 to 50% [250 to 260px]").toString(),
                    regex().exact("\"menu.item-4\" width is ").digits().exact("% [").digits(3).exact("px] which is not in range of 48 to 50% [250 to 260px]").toString(),
                    regex().exact("\"content\" is not centered horizontally inside \"screen\". Offset is ").digits(2).exact("px").toString(),
                    regex().exact("\"greeting\" height is ").digits(2).exact("px which is not in range of 76 to 80px").toString(),
                    regex().exact("\"header.text\" text is \"Freundschaftsbezeigungen\" but should be \"Sample Website\"").toString(),
                    regex().exact("\"menu.item-1\" is ").digits(2).exact("px bottom which is not in range of -2 to 2px").toString(),
                    regex().exact("\"menu.item-1\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-2\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-3\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-3\" is ").digits(2).exact("px top which is not in range of -2 to 2px").toString(),
                    regex().exact("\"menu.item-3\" is -").digits(3).exact("px right of \"menu.item-2\" which is not in range of 0 to 5px").toString(),
                    regex().exact("\"menu.item-2\" is not aligned horizontally all with \"menu.item-3\". Offset is ").digits(2).exact("px").toString(),
                    regex().exact("\"menu.item-4\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-4\" is ").digits(2).exact("px top which is not in range of -2 to 2px").toString(),
                    regex().exact("\"header.text\" text is \"Freundschaftsbezeigungen\" but should be \"Sample Website for Galen Framework\"").toString(),
                    regex().exact("\"menu.item-1\" is ").digits(2).exact("px bottom which is not in range of -2 to 2px").toString(),
                    regex().exact("\"menu.item-1\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-2\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-3\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-4\" width is ").digits(3).exact("px which is not in range of 90 to 130px").toString(),
                    regex().exact("\"menu.item-4\" is ").digits(2).exact("px top which is not in range of -2 to 2px").toString(),
                    regex().exact("\"menu.item-4\" is -").digits(3).exact("px right of \"menu.item-3\" which is not in range of 0 to 5px").toString(),
                    regex().exact("\"menu.item-3\" is not aligned horizontally all with \"menu.item-4\". Offset is ").digits(2).exact("px").toString()
            ));
        } catch (Exception ex) {
            throw new RuntimeException("Report validation failed:\n" +
                    "Html Report: " + htmlReportPath + "/report.html\n" +
                    "Json Report: " + jsonReportPath + "/report.json", ex);
        }
    }

    private void assertErrorMessages(List<String> errorMessages, List<String> expected) {
        assertThat(errorMessages.size(), is(expected.size()));

        Iterator<String> expectedIterator = expected.iterator();

        int number = 0;
        for (String errorMessage : errorMessages) {
            number ++;

            assertThat("Error message #" + number + " should match", errorMessage, matchesPattern(expectedIterator.next()));
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
