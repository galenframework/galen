package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageActions;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.suite.GalenSuiteReader;

import org.testng.annotations.Test;

public class GalenSuiteReaderTest {

    
    @Test public void readSuite_successfully() {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-simple.test").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(2));
        /* Checking suite 1*/
        {
            GalenSuite suite = galenSuites.get(0);
            assertThat(suite.getName(), is("This is a name of suite"));
            assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(2));
            // Checking page 1
            {
                GalenPageTest page = suite.getPageTests().get(0);
                assertThat(page.getUrl(), is("http://example.com/page1"));
                assertThat(page.getScreenSize(), is(new Dimension(640, 480)));
                
                assertThat(page.getActions(), is(actions(GalenPageActions.injectJavascript("javascript.js"),
                        GalenPageActions.check(asList("page1.spec")).withIncludedTags(asList("mobile", "tablet")).withExcludedTags(asList("nomobile")),
                        GalenPageActions.injectJavascript("javascript2.js"),
                        GalenPageActions.seleniumJS("selenium/loginToMyProfile.js").withArguments("{\"login\":\"user1\", \"password\": \"test123\"}"),
                        GalenPageActions.check(asList("page1_1.spec", "page1_2.spec", "page1_3.spec")).withIncludedTags(asList("someTag"))
                        )));
            }
            
            //Checking page 2
            {
                GalenPageTest page = suite.getPageTests().get(1);
                assertThat(page.getUrl(), is("http://example.com/page2"));
                assertThat(page.getScreenSize(), is(new Dimension(1024, 768)));
                
                assertThat(page.getActions(), is(actions(GalenPageActions.check(asList("page2.spec")),
                        (GalenPageAction)GalenPageActions.check(asList("page3.spec")))));
            }
        }
        
        // Checking suite 2
        {
            GalenSuite suite = galenSuites.get(1);
            assertThat(suite.getName(), is("This is another suite name"));
            assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
            
            GalenPageTest page = suite.getPageTests().get(0);
            assertThat(page.getUrl(), is("http://example.com/page3"));
            assertThat(page.getScreenSize(), is(new Dimension(320, 240)));
            
            assertThat(page.getActions(), is(actions(GalenPageActions.check(asList("page3.spec")))));
        }
    }

    private List<GalenPageAction> actions(GalenPageAction...actions) {
        List<GalenPageAction> list = new LinkedList<GalenPageAction>();
        for (GalenPageAction action : actions) {
            list.add(action);
        }
        
        return list;
    }
    
    
}
