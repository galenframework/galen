/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.BrowserFactory;
import net.mindengine.galen.browser.SeleniumGridBrowserFactory;
import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageActions;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.suite.reader.GalenSuiteReader;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GalenSuiteReaderTest {

    private static final Object EMPTY_TAGS = new LinkedList<String>();

    @Test public void shouldRead_simpleSuite_successfully() throws IOException {
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
                assertThat(page.getTitle(), is("This is title for page"));
                assertThat(page.getUrl(), is("http://example.com/page1"));
                assertThat(page.getScreenSize(), is(new Dimension(640, 480)));
                
                assertThat(page.getActions(), is(actions(GalenPageActions.injectJavascript("javascript.js"),
                        GalenPageActions.check(asList("page1.spec")).withIncludedTags(asList("mobile", "tablet")).withExcludedTags(asList("nomobile")),
                        GalenPageActions.injectJavascript("javascript2.js"),
                        GalenPageActions.runJavascript("selenium/loginToMyProfile.js").withArguments("{\"login\":\"user1\", \"password\": \"test123\"}"),
                        GalenPageActions.check(asList("page1_1.spec", "page1_2.spec", "page1_3.spec")).withIncludedTags(asList("sometag"))
                        )));
            }
            
            //Checking page 2
            {
                GalenPageTest page = suite.getPageTests().get(1);
                assertThat(page.getTitle(), is("http://example.com/page2    1024x768"));
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

    
    @Test public void shouldRead_suiteWithVariables_successfully() throws IOException {
        
        System.setProperty("some.system.property", "custom property");
        
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-variables.txt").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(2));
        
        /* Checking suite 1*/
        {
            GalenSuite suite = galenSuites.get(0);
            assertThat(suite.getName(), is("This is a name of suite"));
            assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
            // Checking page 1
            
            GalenPageTest page = suite.getPageTests().get(0);
            assertThat(page.getUrl(), is("http://example.com/some-page.html"));
            assertThat(page.getScreenSize(), is(new Dimension(640, 480)));
            
            assertThat(page.getActions(), is(actions(
                    GalenPageActions.runJavascript("selenium/loginToMyProfile.js").withArguments("{\"myvar\" : \"suite\", \"var_concat\" : \"some-page.html and 640x480\"}")
                    )));
       
        }
        
        // Checking suite 2
        {
            GalenSuite suite = galenSuites.get(1);
            assertThat(suite.getName(), is("This is a name of suite 2 and also custom property"));
            assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
            
            GalenPageTest page = suite.getPageTests().get(0);
            assertThat(page.getUrl(), is("http://example.com/some-page.html"));
            assertThat(page.getScreenSize(), is(new Dimension(640, 480)));
            
            assertThat(page.getActions(), is(actions(
                    GalenPageActions.runJavascript("selenium/loginToMyProfile.js").withArguments("{\"myvar\" : \"suite 2\"}")
                    )));
        }
    }
    
    
    @SuppressWarnings("unchecked")
    @Test public void shouldRead_suiteWithParameterizations_successfully() throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-parameterized.test").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(11));
        
        /* Checking first group of suites */
        {
            Object [][] table = new Object[][]{
                {new Dimension(320, 240), asList("mobile"), "Phone", asList("nomobile")},
                {new Dimension(640, 480), asList("tablet"), "Tablet", EMPTY_TAGS}
            };
            for (int i=0; i<2; i++) {
                GalenSuite suite = galenSuites.get(i);
                assertThat(suite.getName(), is("Test for " + table[i][2]));
                assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
                // Checking page 1
                
                GalenPageTest page = suite.getPageTests().get(0);
                assertThat(page.getUrl(), is("http://example.com/page1"));
                assertThat(page.getScreenSize(), is((Dimension)table[i][0]));
                
                assertThat(page.getActions(), is(actions(
                        GalenPageActions.check(asList("page1.spec")).withIncludedTags((List<String>) table[i][1]).withExcludedTags((List<String>) table[i][3])
                        )));
            }
        }
        
        /* Checking second group of suites */
        {
            Object [][] table = new Object[][]{
                {new Dimension(320, 240), asList("mobile"), "Phone", asList("nomobile"),  "page1"},
                {new Dimension(640, 480), asList("tablet"), "Tablet", EMPTY_TAGS,          "page2"},
                {new Dimension(1024, 768), asList("desktop"), "Desktop", asList("nodesktop"), "page3"}
            };
            for (int i=2; i<5; i++) {
                int j = i - 2;
                GalenSuite suite = galenSuites.get(i);
                assertThat(suite.getName(), is("Test combining 2 tables for " + table[j][2]));
                assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
                // Checking page 1
                
                GalenPageTest page = suite.getPageTests().get(0);
                assertThat(page.getUrl(), is("http://example.com/" + table[j][4]));
                assertThat(page.getScreenSize(), is((Dimension)table[j][0]));
                
                assertThat(page.getActions(), is(actions(
                        GalenPageActions.check(asList("page1.spec")).withIncludedTags((List<String>) table[j][1]).withExcludedTags((List<String>) table[j][3])
                        )));
            }
        }
        
        /* Checking 3rd group of suites */
        {
            
            Object[][] table = new Object[][]{
                {new Dimension(320, 240), asList("mobile"), "Phone", asList("nomobile"),  "page1", "firefox", "Firefox", "any"},
                {new Dimension(640, 480), asList("tablet"), "Tablet", EMPTY_TAGS,          "page2", "firefox", "Firefox", "any"},
                
                {new Dimension(320, 240), asList("mobile"), "Phone", asList("nomobile"),  "page1", "ie", "IE 8", "8"},
                {new Dimension(640, 480), asList("tablet"), "Tablet", EMPTY_TAGS,          "page2", "ie", "IE 8", "8"},
                
                {new Dimension(320, 240), asList("mobile"), "Phone", asList("nomobile"),  "page1", "ie", "IE 9", "9"},
                {new Dimension(640, 480), asList("tablet"), "Tablet", EMPTY_TAGS,          "page2", "ie", "IE 9", "9"},
            };
            
            
            for (int i=5; i<11; i++) {
                int j = i - 5;
                GalenSuite suite = galenSuites.get(i);
                assertThat(suite.getName(), is("Test using 2 layer tables in browser " + table[j][6] + " for type " + table[j][2]));
                assertThat("Amount of pages for 1st suite should be", suite.getPageTests().size(), is(1));
                // Checking page 1
                
                GalenPageTest page = suite.getPageTests().get(0);
                assertThat(page.getBrowserFactory(), is((BrowserFactory)new SeleniumGridBrowserFactory("http://mygrid:8080/wd/hub")
                                                          .withBrowser((String)table[j][5])
                                                          .withBrowserVersion((String)table[j][7])
                ));
                assertThat(page.getUrl(), is("http://example.com/" + table[j][4]));
                assertThat(page.getScreenSize(), is((Dimension)table[j][0]));
                
                assertThat(page.getActions(), is(actions(
                        GalenPageActions.check(asList("page1.spec")).withIncludedTags((List<String>) table[j][1]).withExcludedTags((List<String>) table[j][3])
                        )));
            }
        }
       
    }
    
    @Test
    public void shouldParse_suitesWithEmptyUrls() throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-empty-url.test").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(4));
        
        for (int i = 0; i < 4; i++) {
            assertThat(galenSuites.get(i).getName(), is("Suite " + (i+1)));
            GalenPageTest pageTest = galenSuites.get(i).getPageTests().get(0);
            assertThat(pageTest.getUrl(), is(nullValue()));
        }
        
        assertThat(galenSuites.get(0).getPageTests().get(0).getScreenSize(), is(new Dimension(640, 480)));
        assertThat(galenSuites.get(1).getPageTests().get(0).getScreenSize(), is(nullValue()));
        assertThat(galenSuites.get(2).getPageTests().get(0).getScreenSize(), is(new Dimension(320, 240)));
        assertThat(galenSuites.get(3).getPageTests().get(0).getScreenSize(), is(nullValue()));
    }
    
    @Test
    public void shouldNotInclude_disabledSuites() throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-disabled.test").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(3));
        assertThat(galenSuites.get(0).getName(), is("Suite 1"));
        assertThat(galenSuites.get(1).getName(), is("Suite 2"));
        assertThat(galenSuites.get(2).getName(), is("Suite 3"));
    }
    
    @Test
    public void shouldIncludeEverything_forImportedTestSuites() throws IOException {
        GalenSuiteReader reader = new GalenSuiteReader();
        
        List<GalenSuite> galenSuites = reader.read(new File(getClass().getResource("/suites/suite-import.test").getFile()));
        
        assertThat("Amount of suites should be", galenSuites.size(), is(3));
        assertThat(galenSuites.get(0).getName(), is("Suite 1"));
        assertThat(galenSuites.get(1).getName(), is("Suite 2"));
        assertThat(galenSuites.get(2).getName(), is("Suite 3 imported test suite name"));
    }
    
    
    
    
    private List<GalenPageAction> actions(GalenPageAction...actions) {
        List<GalenPageAction> list = new LinkedList<GalenPageAction>();
        for (GalenPageAction action : actions) {
            list.add(action);
        }
        
        return list;
    }
    
    
    
    
    @Test(dataProvider="provideBadSamples") public void shouldGiveError_withLineNumberInformation_whenParsingIncorrectSuite(String filePath, int expectedLine, String expectedMessage) throws IOException {
        FileSyntaxException exception = null;
        try {
            new GalenSuiteReader().read(new File(getClass().getResource(filePath).getFile()));
        }
        catch (FileSyntaxException e) {
            exception = e;
            System.out.println("***************");
            e.printStackTrace();
        }
        
        
        String fullPath = getClass().getResource(filePath).getFile();
        assertThat("Exception should be thrown", exception, notNullValue());
        assertThat("Message should be", exception.getMessage(), is(expectedMessage + "\n    in " + fullPath + ":" + expectedLine));
        assertThat("Line should be", exception.getLine(), is(expectedLine));
        
    }
    
    
    @DataProvider public Object[][] provideBadSamples() {
        return new Object[][]{
            {"/suites/suite-with-error-unknown-table-in-parameterized.test", 16, "Table with name \"some_unknown_table\" does not exist"},
            {"/suites/suite-with-error-page-error.test", 3, "Incorrect amount of arguments: selenium http://"},
            {"/suites/suite-with-error-action-inject-error.test", 3, "Cannot parse:         inject"},
            {"/suites/suite-with-error-table-wrong-amount-of-columns-1.test", 5, "Amount of cells in a row is not the same in header"},
            {"/suites/suite-with-error-table-wrong-amount-of-columns-2.test", 4, "Incorrect format. Should end with '|'"},
            {"/suites/suite-with-error-table-wrong-amount-of-columns-3.test", 4, "Incorrect format. Should start with '|'"},
            {"/suites/suite-with-error-parameterization-merge-tables.test", 12, "Cannot merge table \"table2\". Perhaps it has different amount of columns"},
            {"/suites/suite-with-error-parameterization-wrong-amount-of-columns.test", 5, "Amount of cells in a row is not the same in header"},
            {"/suites/suite-with-error-wrong-indentation-1.test", 8, "Incorrect indentation. Amount of spaces in indentation should be the same within one level"},
            {"/suites/suite-with-error-wrong-indentation-2.test", 6, "Incorrect indentation. Should use from 1 to 8 spaces"}
        };
    }
}
