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
package net.mindengine.galen.utils;

import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ReportUtil {

    /**
     * Analyses a given Galen layout report and create a readable exception message if any errors occured
     * 
     * @param currentDriver
     *            webdriver instance to use
     * @param layoutReport
     *            report to anaylse
     * @param specPath
     *            used spec
     * @param testDevice
     *            used test device
     * @param tags
     *            used tags
     */
    public static void analyzeReport(final WebDriver currentDriver, final LayoutReport layoutReport, final String specPath, final TestDevice testDevice) {
        if (layoutReport.errors() > 0) {
            final StringBuilder errorDetails = new StringBuilder();
            for (final LayoutSection layoutSection : layoutReport.getSections()) {
                final StringBuilder errorElementDetails = new StringBuilder();
                errorElementDetails.append("\n").append("Layout Section: ").append(layoutSection.getName()).append("\n");
                errorElementDetails.append("  ViewPort Details: ").append(testDevice).append("\n");
                for (final LayoutObject layoutObject : layoutSection.getObjects()) {
                    boolean hasErrors = false;
                    errorElementDetails.append("  Element: ").append(layoutObject.getName());
                    for (final LayoutSpec layoutSpec : layoutObject.getSpecs()) {
                        if (layoutSpec.getErrors() != null && layoutSpec.getErrors().size() > 0) {
                            errorElementDetails.append(layoutSpec.getErrors().toString());
                            hasErrors = true;
                        }
                    }
                    if (hasErrors) {
                        errorDetails.append(errorElementDetails).append("\n");
                        errorDetails.append("  Spec: ").append(specPath).append("\n");
                    }
                }
            }
            if (currentDriver instanceof RemoteWebDriver) {
                final String browser = ((RemoteWebDriver) currentDriver).getCapabilities().getBrowserName() + " "
                        + ((RemoteWebDriver) currentDriver).getCapabilities().getVersion();
                final Platform platform = ((RemoteWebDriver) currentDriver).getCapabilities().getPlatform();
                throw new RuntimeException("Browser: " + browser + " on " + platform + ", device " + testDevice + ", more details here: "
                        + errorDetails.toString());
            } else {
                throw new RuntimeException(errorDetails.toString());
            }
        }
    }
}
