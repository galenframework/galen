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

import java.awt.Dimension;

import net.mindengine.galen.runner.GalenArguments;

import org.apache.commons.cli.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArgumentParserTest {

    private static final String[] EMPTY_TAGS = {};

    @Test(dataProvider = "provideGoodSamples")
    public void shoulParseArguments(SimpleArguments args,  GalenArguments expectedArguments) throws ParseException {
        GalenArguments realArguments = GalenArguments.parse(args.args);
        assertThat(realArguments, is(expectedArguments));
    }
    
    @Test
    public void shouldParseSystemProperties() throws ParseException {
        GalenArguments.parse(new String[]{"test", ".", "--htmlreport", "report", "-DsomeCustomVar=123", "-DsomeOtherVar=456"});
        assertThat(System.getProperty("someCustomVar"), is("123"));
        assertThat(System.getProperty("someOtherVar"), is("456"));
    }
    
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            {args("test", "mysuite", 
                            "--recursive", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenArguments()
                    .withAction("test")
                    .withPaths(asList("mysuite"))
                    .withRecursive(true)
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")
                    .withIncludedTags(EMPTY_TAGS)
                    .withExcludedTags(EMPTY_TAGS)},
                    
            {args("test", "mysuite", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenArguments()
                    .withAction("test")
                    .withPaths(asList("mysuite"))
                    .withRecursive(false)
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")
                    .withIncludedTags(EMPTY_TAGS)
                    .withExcludedTags(EMPTY_TAGS)},
                    
            {args("test", "mysuite", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml",
                            "--parallel-suites", "4"), 
                new GalenArguments()
                    .withAction("test")
                    .withPaths(asList("mysuite"))
                    .withRecursive(false)
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")
                    .withIncludedTags(EMPTY_TAGS)
                    .withExcludedTags(EMPTY_TAGS)
                    .withParallelSuites(4)},
                    
            {args("test", "mysuite", "mysuite2", 
                            "--recursive", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenArguments()
                    .withAction("test")
                    .withPaths(asList("mysuite", "mysuite2"))
                    .withRecursive(true)
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")
                    .withIncludedTags(EMPTY_TAGS)
                    .withExcludedTags(EMPTY_TAGS)},
                    
            {args("check",  "some.spec",
                            "--url", "http://mindengine.net", 
                            "--javascript", "some.js", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withPaths(asList("some.spec"))
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")},

            {args("check", "some.spec", "-u", "http://mindengine.net", 
                            "-j", "some.js", 
                            "-i", "mobile,all", 
                            "-e", "nomobile,testTag", 
                            "-s", "400x700", 
                            "-H", "some.html",
                            "-g", "testng.xml"), 
               new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withPaths(asList("some.spec"))
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")},

            {args("check", "some.spec",
                            "--url", "http://mindengine.net", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--htmlreport", "some.html"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withPaths(asList("some.spec"))
                    .withHtmlReport("some.html")},
                   
            {args("check", "some1.spec", "some2.spec", "--url", "http://mindengine.net"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags()
                    .withExcludedTags()
                    .withPaths(asList("some1.spec", "some2.spec"))},
           
            {args("config"), 
                new GalenArguments()
                    .withAction("config")},
        };
    }
    
    
    @Test(dataProvider="provideBadSamples")
    public void shouldGiveError_forIncorrectArguments(String expectedErrorMessage, SimpleArguments args) throws ParseException {
        IllegalArgumentException exception = null;
        try {
            GalenArguments.parse(args.args);
        }
        catch(IllegalArgumentException ex) {
            exception = ex;
        }
        
        assertThat("Exception should be", exception, is(notNullValue()));
        assertThat("Error message should be", exception.getMessage(), is(expectedErrorMessage));
    }
    
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][]{
          {"Incorrect size: 123", 
              args("check", "some.spec", "--url", "http://example.com", "--size", "123")},
          
          {"Incorrect size: 123xx123", 
              args("check", "some.spec", "--url", "http://example.com", "--size", "123xx123")},
          
          {"Incorrect size: a123xx123", 
              args("check", "some.spec", "--url", "http://example.com", "--size", "a123xx123")},
          
          {"Incorrect size: 123x", 
              args("check", "some.spec", "--url", "http://example.com", "--size", "123x")},
          
          {"Missing value for url",
              args("check", "some.spec", 
                  "--url", 
                  "--javascript", "some.js", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--htmlreport", "some.html")},
                  
          {"Missing value for javascript",
              args("check", "some.spec", 
                  "--url", "http://example.com", 
                  "--javascript", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--htmlreport", "some.html")},
                  
          {"Missing value for include",
              args("check", "some.spec", 
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--htmlreport", "some.html")},
                 
          {"Missing value for exclude",
              args("check", "some.spec", 
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", 
                  "--size", "400x700", 
                  "--htmlreport", "some.html")},
                  
          {"Missing value for size",
              args("check", "some.spec", 
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", "nomobile", 
                  "--size", 
                  "--htmlreport", "some.html")},
                  
          {"Missing value for htmlreport",
                  args("check", "some.spec", 
                      "--url", "http://example.com", 
                      "--javascript", "script.js", 
                      "--include", "mobile", 
                      "--exclude", "nomobile", 
                      "--size", "540x350", 
                      "--htmlreport")},
                  
          {"Missing spec files",
              args("check", 
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", "nomobile", 
                  "--size", "540x350")},
                  
          {"Missing test files",
              args("test", 
                  "--htmlreport", "reports")}

        };
    }

    private class SimpleArguments {
        private String[] args;

        private SimpleArguments(String...args) {
            this.args = args;
        }
        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            for (String arg: args) {
                buffer.append(arg);
                buffer.append(" ");
            }
            return buffer.toString();
        }
    }

    private SimpleArguments args(String...args) {
        return new SimpleArguments(args);
    }

}
