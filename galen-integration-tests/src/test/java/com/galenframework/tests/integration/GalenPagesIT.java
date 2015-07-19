package com.galenframework.tests.integration;

import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionRunJavascript;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionRunJavascript;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GalenPagesIT {

    private static WebDriver driver;

    public static final List<String> _callbacks = new LinkedList<>();

    @BeforeClass
    public void initDriver() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public void closeDriver() {
        driver.quit();
    }

    @Test
    public void listComponents_fetchingAndInteraction_onGalenPages() throws Exception {
        driver.get(getUrlToResource("/complex-page/index.html"));

        GalenPageActionRunJavascript actionRunJs = new GalenPageActionRunJavascript("/complex-page/list.test.js");
        actionRunJs.execute(new TestReport(), new SeleniumBrowser(driver), new GalenPageTest(), null);

        assertThat(_callbacks, is(asList("Amount of comments is 3",
                "2nd user name is: Piet",
                "2nd message is: OMG!")));
    }

    private String getUrlToResource(String resource) {
        return getClass().getResource(resource).toExternalForm();
    }
}
