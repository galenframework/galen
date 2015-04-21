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
package net.mindengine.galen.testng;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;

public abstract class GalenTestBase {

    ThreadLocal<TestReport> report = new ThreadLocal<TestReport>();

    public void checkLayout(WebDriver driver, String specPath, List<String> includedTags) throws IOException {
        String title = "Check layout " + specPath;
        LayoutReport layoutReport = Galen.checkLayout(driver, specPath, includedTags, null, new Properties(), null);
        report.get().layout(layoutReport, title);

        if (layoutReport.errors() > 0) {
            throw new RuntimeException("Incorrect layout: " + title);
        }
    }

    @BeforeMethod
    public void initReport(Method method) {
        report.set(GalenReportsContainer.get().registerTest(method));
    }

}
