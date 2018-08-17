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
package com.galenframework.tests.reports;

import com.galenframework.reports.TestReport;
import com.galenframework.reports.model.FileTempStorage;
import com.galenframework.reports.nodes.*;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestReportTest {


    @Test
    public void shouldAllow_toStore_extrasData() throws NoSuchFieldException, IllegalAccessException {
        resetFileStorageUniqueId();

        TestReport report = new TestReport();

        report.info("Some info")
                .withExtrasText("debug-message", "some debug value")
                .withExtrasLink("link", "http://example.com")
                .withExtrasFile("someFile", new File(getClass().getResource("/some-report-attachment.txt").getFile()))
                .withExtrasImage("screenshot", new File(getClass().getResource("/imgs/page-screenshot.png").getFile()));

        Map<String, ReportExtra> extras = report.getNodes().get(0).getExtras();

        ReportExtraText extraText = (ReportExtraText) extras.get("debug-message");
        assertThat(extraText.getValue(), is("some debug value"));

        ReportExtraLink extraLink = (ReportExtraLink) extras.get("link");
        assertThat(extraLink.getValue(), is("http://example.com"));

        ReportExtraFile extraFile = (ReportExtraFile) extras.get("someFile");
        assertThat(extraFile.getValue(), is("file-1-some-report-attachment.txt"));

        ReportExtraImage extraImage = (ReportExtraImage) extras.get("screenshot");
        assertThat(extraImage.getValue(), is("file-2-page-screenshot.png"));


    }

    private void resetFileStorageUniqueId() throws NoSuchFieldException, IllegalAccessException {
        Field uniqueIdField = FileTempStorage.class.getDeclaredField("_uniqueId");
        uniqueIdField.setAccessible(true);
        uniqueIdField.set(null, 0L);
    }
}
