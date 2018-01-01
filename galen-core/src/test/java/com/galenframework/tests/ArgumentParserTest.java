/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import com.galenframework.actions.*;
import com.galenframework.runner.CombinedListener;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArgumentParserTest {

    private static final List<String> EMPTY_TAGS = Collections.emptyList();
    public static final CombinedListener NO_LISTENER = null;


    @Test
    public void shouldParseSystemProperties() throws ParseException {
        GalenAction.create("test",
                new String[]{".", "--htmlreport", "report", "-DsomeCustomVar=123", "-DsomeOtherVar=456", "-DpageUrl=http://example.com?q=1&w=2"},
                System.out, System.err, NO_LISTENER);

        assertThat(System.getProperty("someCustomVar"), is("123"));
        assertThat(System.getProperty("someOtherVar"), is("456"));
        assertThat(System.getProperty("pageUrl"), is("http://example.com?q=1&w=2"));
    }

    @Test(dataProvider = "goodSamples_testAction")
    public void shouldParse_testActionArguments(SimpleArguments args, GalenActionTestArguments expectedArguments) {
        String actionName = args.args[0];
        String[] arguments = ArrayUtils.subarray(args.args, 1, args.args.length);
        GalenActionTest action = (GalenActionTest) GalenAction.create(actionName, arguments, System.out, System.err, NO_LISTENER);
        assertThat(action.getTestArguments(), is(expectedArguments));
    }
    
    @DataProvider
    public Object[][] goodSamples_testAction() {
        return new Object[][]{
            {args("test", "mysuite", 
                            "--recursive", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml",
                            "--jsonreport", "json-reports"
                    ),
                new GalenActionTestArguments()
                    .setPaths(asList("mysuite"))
                    .setRecursive(true)
                    .setHtmlReport("some.html")
                    .setTestngReport("testng.xml")
                    .setJsonReport("json-reports")
                    .setIncludedTags(EMPTY_TAGS)
                    .setExcludedTags(EMPTY_TAGS)},

            {args("test", "mysuite",
                    "--groups", "mobile,tablet,homepage"),
                    new GalenActionTestArguments()
                            .setPaths(asList("mysuite"))
                            .setGroups(asList("mobile", "tablet", "homepage"))
                            .setRecursive(false)
                            .setIncludedTags(EMPTY_TAGS)
                            .setExcludedTags(EMPTY_TAGS)},

            {args("test", "mysuite",
                    "--excluded-groups", "mobile,tablet,homepage"),
                    new GalenActionTestArguments()
                            .setPaths(asList("mysuite"))
                            .setExcludedGroups(asList("mobile", "tablet", "homepage"))
                            .setRecursive(false)
                            .setIncludedTags(EMPTY_TAGS)
                            .setExcludedTags(EMPTY_TAGS)},


            {args("test", "mysuite",
                        "--htmlreport", "some.html",
                        "--testngreport", "testng.xml"),
                new GalenActionTestArguments()
                    .setPaths(asList("mysuite"))
                    .setRecursive(false)
                    .setHtmlReport("some.html")
                    .setTestngReport("testng.xml")
                    .setIncludedTags(EMPTY_TAGS)
                    .setExcludedTags(EMPTY_TAGS)},
                    
            {args("test", "mysuite", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml",
                            "--parallel-suites", "4"), 
                new GalenActionTestArguments()
                    .setPaths(asList("mysuite"))
                    .setRecursive(false)
                    .setHtmlReport("some.html")
                    .setTestngReport("testng.xml")
                    .setIncludedTags(EMPTY_TAGS)
                    .setExcludedTags(EMPTY_TAGS)
                    .setParallelThreads(4)},
                    
            {args("test", "mysuite", "mysuite2", 
                            "--recursive", 
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenActionTestArguments()
                    .setPaths(asList("mysuite", "mysuite2"))
                    .setRecursive(true)
                    .setHtmlReport("some.html")
                    .setTestngReport("testng.xml")
                    .setIncludedTags(EMPTY_TAGS)
                    .setExcludedTags(EMPTY_TAGS)},
                    
            {args("test", "mysuite", "mysuite2", 
                            "--filter", "Some Test *"), 
                new GalenActionTestArguments()
                    .setPaths(asList("mysuite", "mysuite2"))
                    .setRecursive(false)
                    .setFilter("Some Test *")
                    .setIncludedTags(EMPTY_TAGS)
                    .setExcludedTags(EMPTY_TAGS)},

            {args("test", "mysuite", "mysuite2", "--parallel-tests", "3"),
                    new GalenActionTestArguments()
                            .setPaths(asList("mysuite", "mysuite2"))
                            .setRecursive(false)
                            .setParallelThreads(3)
                            .setIncludedTags(EMPTY_TAGS)
                            .setExcludedTags(EMPTY_TAGS)},


            {args("test", "mysuite", "mysuite2", "--config", "/some/config"),
                    new GalenActionTestArguments()
                            .setPaths(asList("mysuite", "mysuite2"))
                            .setRecursive(false)
                            .setIncludedTags(EMPTY_TAGS)
                            .setExcludedTags(EMPTY_TAGS)
                            .setConfig("/some/config")
            },
        };
    }

    @Test(dataProvider = "goodSamples_simpleActions")
    public void shouldParse_simpleActions(String firstArg, Class<?>expectedType) {
        GalenAction action = GalenAction.create(firstArg, new String[]{}, System.out, System.err, NO_LISTENER);
        assertThat(action, is(instanceOf(expectedType)));
    }

    @DataProvider
    public Object[][] goodSamples_simpleActions() {
        return new Object[][] {
                {"config", GalenActionConfig.class},
                {"help", GalenActionHelp.class},
                {"-h", GalenActionHelp.class},
                {"--help", GalenActionHelp.class},
                {"version", GalenActionVersion.class},
                {"-v", GalenActionVersion.class},
                {"--version", GalenActionVersion.class}
        };
    }

    @Test
    public void shouldParse_dumpAction() {
        GalenActionDump action = (GalenActionDump) GalenAction.create("dump",
                new String[]{"my-page.gspec", "--url", "http://mindengine.net", "--export", "export-page-dir", "--max-width", "100", "--max-height", "150"},
                System.out, System.err, NO_LISTENER);
        assertThat(action.getDumpArguments(), is(new GalenActionDumpArguments()
                .setPaths(asList("my-page.gspec"))
                .setUrl("http://mindengine.net")
                .setExport("export-page-dir")
                .setMaxWidth(100)
                .setMaxHeight(150)));
    }

    @Test
    public void shouldParse_dumpAction_withConfig() {
        GalenActionDump action = (GalenActionDump) GalenAction.create("dump",
                new String[]{"my-page.gspec",
                        "--url", "http://mindengine.net",
                        "--export", "export-page-dir",
                        "--max-width", "100",
                        "--max-height", "150",
                        "--config", "/some/config"
                },
                System.out, System.err, NO_LISTENER);
        assertThat(action.getDumpArguments(), is(new GalenActionDumpArguments()
                .setPaths(asList("my-page.gspec"))
                .setUrl("http://mindengine.net")
                .setExport("export-page-dir")
                .setMaxWidth(100)
                .setMaxHeight(150)
                .setConfig("/some/config")
        ));
    }

    @Test
    public void should_parse_generate_action() {
        GalenActionGenerate action = (GalenActionGenerate) GalenAction.create("generate",
            new String []{
                "path/to/some/page-dump.json",
                "--export", "destination.gspec"
            },
            System.out, System.err, NO_LISTENER
        );
        assertThat(action.getGenerateArguments(), is(new GalenActionGenerateArguments()
            .setPath("path/to/some/page-dump.json")
            .setExport("destination.gspec")
        ));
    }

    @Test
    public void should_parse_generate_action_with_galenextras_disabled() {
        GalenActionGenerate action = (GalenActionGenerate) GalenAction.create("generate",
            new String []{
                "path/to/some/page-dump.json",
                "--export", "destination.gspec",
                "--no-galen-extras"
            },
            System.out, System.err, NO_LISTENER
        );
        assertThat(action.getGenerateArguments(), is(new GalenActionGenerateArguments()
            .setPath("path/to/some/page-dump.json")
            .setExport("destination.gspec")
            .setUseGalenExtras(false)
        ));
    }

    @Test(dataProvider = "goodSamples_checkAction")
    public void shouldParse_checkActionArguments(SimpleArguments args, GalenActionCheckArguments expectedArguments) {
        String actionName = args.args[0];
        String[] arguments = ArrayUtils.subarray(args.args, 1, args.args.length);
        GalenActionCheck action = (GalenActionCheck) GalenAction.create(actionName, arguments, System.out, System.err, NO_LISTENER);
        assertThat(action.getCheckArguments(), is(expectedArguments));
    }
    @DataProvider
    public Object[][] goodSamples_checkAction() {
        return new Object[][]{
                {args("check", "some.spec",
                        "--url", "http://mindengine.net",
                        "--javascript", "some.js",
                        "--include", "mobile,all",
                        "--exclude", "nomobile,testTag",
                        "--size", "400x700",
                        "--htmlreport", "some.html",
                        "--testngreport", "testng.xml",
                        "--junitreport", "junit.xml"),
                        new GalenActionCheckArguments()
                                .setUrl("http://mindengine.net")
                                .setJavascript("some.js")
                                .setIncludedTags(asList("mobile", "all"))
                                .setExcludedTags(asList("nomobile", "testTag"))
                                .setScreenSize(new Dimension(400, 700))
                                .setPaths(asList("some.spec"))
                                .setHtmlReport("some.html")
                                .setTestngReport("testng.xml")
                                .setJunitReport("junit.xml")
                },

                {args("check", "some.spec",
                    "--url", "http://mindengine.net",
                    "--include", "mobile,all",
                    "--exclude", "nomobile,testTag",
                    "--size", "400x700",
                    "--htmlreport", "some.html"),
                    new GalenActionCheckArguments()
                            .setUrl("http://mindengine.net")
                            .setIncludedTags(asList("mobile", "all"))
                            .setExcludedTags(asList("nomobile", "testTag"))
                            .setScreenSize(new Dimension(400, 700))
                            .setPaths(asList("some.spec"))
                            .setHtmlReport("some.html")
                },

                {args("check", "some1.spec", "some2.spec", "--url", "http://mindengine.net"),
                    new GalenActionCheckArguments()
                            .setUrl("http://mindengine.net")
                            .setPaths(asList("some1.spec", "some2.spec"))
                },

                {args("check", "some1.spec", "some2.spec", "--url", "http://mindengine.net", "--config", "/some/config"),
                        new GalenActionCheckArguments()
                                .setUrl("http://mindengine.net")
                                .setPaths(asList("some1.spec", "some2.spec"))
                                .setConfig("/some/config")
                },
        };
    }
    
    
    @Test(dataProvider="provideBadSamples")
    public void shouldGiveError_forIncorrectArguments(String expectedErrorMessage, SimpleArguments args) throws ParseException {
        IllegalArgumentException exception = null;
        try {
            String actionName = args.args[0];
            String[] arguments = ArrayUtils.subarray(args.args, 1, args.args.length);
            GalenAction.create(actionName, arguments, System.out, System.err, NO_LISTENER);
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
