package net.mindengine.galen.testng;

import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;
import net.mindengine.galen.utils.TestDevice;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ReportUtil {

    /**
     * 
     * @param currentDriver
     * @param layoutReport
     * @param specPath
     * @param testDevice
     * @param tags
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
