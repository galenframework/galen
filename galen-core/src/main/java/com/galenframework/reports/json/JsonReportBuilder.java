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
package com.galenframework.reports.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestIdGenerator;
import com.galenframework.reports.TestReport;
import com.galenframework.reports.GalenTestAggregatedInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.galenframework.utils.GalenUtils.makeSureFolderExists;

/**
 * Created by ishubin on 2015/02/15.
 */
public class JsonReportBuilder {

    private ObjectMapper jsonMapper = createJsonMapper();


    private ObjectMapper createJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private TestIdGenerator testIdGenerator = new TestIdGenerator();


    public void build(List<GalenTestInfo> testInfos, String reportPath) throws IOException {
        ReportOverview reportOverview = createReportOverview(testInfos);

        for (GalenTestAggregatedInfo aggregatedInfo : reportOverview.getTests()) {
            exportTestReportToJson(new JsonTestReport(aggregatedInfo.getTestId(), aggregatedInfo.getTestInfo()), reportPath);
            moveAllReportFiles(aggregatedInfo.getTestInfo().getReport(), reportPath);
        }

        exportReportOverviewToJson(reportOverview, reportPath);
    }


    private void moveAllReportFiles(TestReport report, String reportPath) throws IOException {
        if (report != null && report.getFileStorage() != null) {
            report.getFileStorage().copyAllFilesTo(new File(reportPath));
        }
    }

    public ReportOverview createReportOverview(List<GalenTestInfo> testInfos) {
        ReportOverview reportOverview = new ReportOverview();
        for (GalenTestInfo testInfo : testInfos) {
            String testId = testIdGenerator.generateTestId(testInfo.getName());
            reportOverview.add(new GalenTestAggregatedInfo(testId, testInfo));
        }

        return reportOverview;
    }

    public String exportReportOverviewToJsonAsString(ReportOverview reportOverview) throws JsonProcessingException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportOverview);
    }

    private void exportReportOverviewToJson(ReportOverview reportOverview, String reportPath) throws IOException {
        makeSureFolderExists(reportPath);
        File file = new File(reportPath + File.separator + "report.json");
        file.createNewFile();
        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, reportOverview);
    }

    private void exportTestReportToJson(JsonTestReport aggregatedInfo, String reportPath) throws IOException {
        makeSureFolderExists(reportPath);

        File file = new File(reportPath + File.separator + aggregatedInfo.getTestId() + ".json");
        file.createNewFile();

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, aggregatedInfo);
    }

    public String exportTestReportToJsonString(GalenTestAggregatedInfo info) throws JsonProcessingException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new JsonTestReport(info.getTestId(), info.getTestInfo()));
    }
}
