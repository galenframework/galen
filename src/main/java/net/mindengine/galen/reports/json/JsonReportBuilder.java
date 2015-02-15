package net.mindengine.galen.reports.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mindengine.galen.reports.GalenTestAggregatedInfo;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestIdGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        ReportOverview reportOverview = new ReportOverview();

        for (GalenTestInfo testInfo : testInfos) {
            String testId = testIdGenerator.generateTestId(testInfo.getName());
            reportOverview.add(new GalenTestAggregatedInfo(testId, testInfo));

            exportTestReportToJson(new JsonTestReport(testId, testInfo), reportPath);
        }

        exportReportOverviewToJson(reportOverview, reportPath);
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

    private void makeSureFolderExists(String reportPath) throws IOException {
        FileUtils.forceMkdir(new File(reportPath));
    }
}
