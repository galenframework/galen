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
package net.mindengine.galen.junit;

import java.util.List;

import net.mindengine.galen.api.GalenReportsContainer;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.config.GalenProperty;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.HtmlReportBuilder;
import net.mindengine.galen.reports.model.FileTempStorage;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts JUnit events and creates Galen Report
 */
public class JUnitStepListener extends RunListener {

    private static final Logger LOG = LoggerFactory.getLogger(JUnitStepListener.class);

    /**
     * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
     */
    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
        LOG.info("Generating Galen Html reports");
        List<GalenTestInfo> tests = GalenReportsContainer.get().getAllTests();
        try {
            new HtmlReportBuilder().build(tests, GalenConfig.getConfig().readProperty(GalenProperty.TEST_JAVA_REPORT_OUTPUTFOLDER));
            cleanData(tests);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes temporary test data
     * 
     * @param testInfos
     */
    private void cleanData(List<GalenTestInfo> testInfos) {
        for (GalenTestInfo testInfo : testInfos) {
            if (testInfo.getReport() != null) {
                try {
                    FileTempStorage storage = testInfo.getReport().getFileStorage();
                    if (storage != null) {
                        storage.cleanup();
                    }
                } catch (Exception e) {
                    LOG.error("Unkown error during report cleaning", e);
                }
            }
        }
    }

}
