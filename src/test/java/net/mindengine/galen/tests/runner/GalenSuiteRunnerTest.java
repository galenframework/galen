package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.BrowserFactory;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.components.RecordingSuiteListener;
import net.mindengine.galen.components.TestGroups;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.reader.page.PageSpecReader;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

@Test(groups=TestGroups.SELENIUM)
public class GalenSuiteRunnerTest {

    private static final String TEST_URL = "file://" + GalenPageRunnerTest.class.getResource("/html/page1.html").getPath();
    
    @Test public void runsSuiteSuccessfully_andInvokesSuiteListener() throws IOException {
        List<GalenPageRunner> pageRunners = suite(
                new GalenPageRunner()
                    .withUrl(TEST_URL)
                    .withScreenSize(new Dimension(400, 800))
                    .withIncludedTags(asList("mobile"))
                    .withSpec(new PageSpecReader().read(GalenPageRunnerTest.class.getResourceAsStream("/html/page.spec"))),
                
                new GalenPageRunner()
                    .withUrl(TEST_URL)
                    .withScreenSize(new Dimension(700, 800))
                    .withIncludedTags(asList("mobile"))
                    .withSpec(new PageSpecReader().read(GalenPageRunnerTest.class.getResourceAsStream("/html/page.spec")))
        );
        
        RecordingSuiteListener suiteListener = new RecordingSuiteListener();
        
        GalenSuiteRunner suiteRunner = new GalenSuiteRunner(new BrowserFactory() {
                @Override
                public Browser openBrowser() {
                    return new SeleniumBrowser(new FirefoxDriver());
                }
            })
            .withSuiteListener(suiteListener);
        
        suiteRunner.runSuite(pageRunners);
        
        assertThat(suiteListener.getRecordedInvokations(), is("<suite-started>" +
                "<before-page>" +
                "<after-page errors=1>" +
                "<before-page>" +
                "<after-page errors=2>" +
        		"<suite-finished>"));
    }

    private List<GalenPageRunner> suite(GalenPageRunner...pageRunners) {
        return Arrays.asList(pageRunners);
    }
}
