package net.mindengine.galen.testng;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

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


    public static class TestDevice {
        private final String name;
        private final Dimension screenSize;
        private final List<String> tags;

        public TestDevice(String name, Dimension screenSize, List<String> tags) {
            this.name = name;
            this.screenSize = screenSize;
            this.tags = tags;
        }


        public String getName() {
            return name;
        }

        public Dimension getScreenSize() {
            return screenSize;
        }

        public List<String> getTags() {
            return tags;
        }
    }
}
