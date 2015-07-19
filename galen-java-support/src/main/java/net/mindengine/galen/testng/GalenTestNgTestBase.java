package net.mindengine.galen.testng;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.support.GalenReportsContainer;
import net.mindengine.galen.utils.GalenUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class is used as a base test class for TestNG tests.
 * It takes care of storing WebDriver and Report instances in {@link ThreadLocal} so that the tests could be run in parallel
 * without any race condition. It also quits the WebDriver at the end of the test.
 * It has {@link #checkLayout} method which als takes care of storing the layout report in reports container.
 */
public abstract class GalenTestNgTestBase {

    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected ThreadLocal<TestReport> report = new ThreadLocal<>();

    /**
     * Returns the report for current test thread
     * @return the instance of TestReport for current thread
     */
    public TestReport getReport() {
        TestReport report = this.report.get();
        if (report == null) {
            throw new RuntimeException("The report is not instantiated yet");
        }
        return report;
    }


    /**
     * Loads the given url in the current driver for current test thread
     * @param url The website url that should be loaded in the current driver
     */
    public void load(String url) {
        getDriver().get(url);
    }

    /**
     * Loads the given url in the current driver for current test thread and changes the browser window size
     * @param url The website url that should be loaded in the current driver
     * @param width The width of browser window
     * @param height The height of browser window
     */
    public void load(String url, int width, int height) {
        load(url);
        resize(width, height);
    }

    /**
     * Injects the given javaScript expression in current driver for current test thread
     * @param javaScript A JavaScript code that should be executed in the current browser
     */
    public void inject(String javaScript) {
        GalenUtils.injectJavascript(getDriver(), javaScript);
    }

    /**
     * Changes the size of current browser for current test thread
     * @param width The width of browser window
     * @param height The height of browser window
     */
    public void resize(int width, int height) {
        getDriver().manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Checks layout of the page that is currently open in current thread. Takes driver from {@link ThreadLocal}
     * @param specPath a path to galen spec file
     * @param includedTags a list of tags that should be included in spec
     * @throws IOException
     */
    public void checkLayout(String specPath, List<String> includedTags) throws IOException {
        checkLayout(specPath, includedTags, Collections.<String>emptyList(), new Properties(), null);
    }

    /**
     * Checks layout of the page that is currently open in current thread. Takes driver from ThreadLocal
     * @param specPath a path to galen spec file
     * @param includedTags a list of tags that should be included in spec
     * @param excludedTags a list of tags that should be excluded from spec
     * @param properties a set of properties that will be accessible in special galen spec expressions.
     * @param vars JavaScript variables that will be available in special galen spec expressions
     * @throws IOException
     */
    public void checkLayout(String specPath, List<String> includedTags, List<String> excludedTags, Properties properties, Map<String, Object> vars) throws IOException {
        String title = "Check layout " + specPath;
        LayoutReport layoutReport = Galen.checkLayout(getDriver(), specPath, includedTags, excludedTags, properties, vars);
        getReport().layout(layoutReport, title);

        if (layoutReport.errors() > 0) {
            throw new RuntimeException("Incorrect layout: " + title);
        }
    }

    /**
     * Initializes the TestReport instance with the name of current test method and stores it in {@link ThreadLocal}
     * @param method
     */
    @BeforeMethod
    public void initReport(Method method) {
        report.set(GalenReportsContainer.get().registerTest(method));
    }

    /**
     * Initializes the WebDriver instance and stores it in {@link ThreadLocal}
     * @param args the arguments of current test
     */
    @BeforeMethod
    public void initDriver(Object[] args) {
        WebDriver driver = createDriver(args);
        this.driver.set(driver);
    }

    /**
     * Used in order to initialize a {@link WebDriver}
     * @param args the arguments of current test
     * @return
     */
    public abstract WebDriver createDriver(Object[] args);

    @AfterMethod
    public void quitDriver() {
        getDriver().quit();
    }

    /**
     * Returns {@link WebDriver} instance for current test thread
     * @return a {@link WebDriver} instance for current test thread
     */
    public WebDriver getDriver() {
        WebDriver driver = this.driver.get();
        if (driver == null) {
            throw new RuntimeException("The driver is not instantiated yet");
        }
        return driver;
    }

}
