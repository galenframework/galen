package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.Dimension;

import net.mindengine.galen.runner.GalenArguments;

import org.apache.commons.cli.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArgumentParserTest {

    private static final String[] EMPTY_TAGS = {};

    @Test(dataProvider = "provideGoodSamples")
    public void shoulParseArguments(SimpleArguments args,  GalenArguments expectedArguments) throws ParseException {
        GalenArguments realArguments = GalenArguments.parse(args.args);
        assertThat(realArguments, is(expectedArguments));
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
                    
            {args("check", "--url", "http://mindengine.net", 
                            "--javascript", "some.js", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--spec", "some.spec",
                            "--htmlreport", "some.html",
                            "--testngreport", "testng.xml"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")},

            {args("check", "-u", "http://mindengine.net", 
                            "-j", "some.js", 
                            "-i", "mobile,all", 
                            "-e", "nomobile,testTag", 
                            "-d", "400x700", 
                            "-s", "some.spec",
                            "-H", "some.html",
                            "-g", "testng.xml"), 
               new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")
                    .withTestngReport("testng.xml")},

            {args("check", "--url", "http://mindengine.net", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--spec", "some.spec",
                            "--htmlreport", "some.html"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")},
                   
            {args("check", "--url", "http://mindengine.net", 
                            "--spec", "some.spec"), 
                new GalenArguments()
                    .withAction("check")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags()
                    .withExcludedTags()
                    .withSpec("some.spec")},
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
              args("run", "--url", "http://example.com", "--size", "123")},
          
          {"Incorrect size: 123xx123", 
              args("run", "--url", "http://example.com", "--size", "123xx123")},
          
          {"Incorrect size: a123xx123", 
              args("run", "--url", "http://example.com", "--size", "a123xx123")},
          
          {"Incorrect size: 123x", 
              args("run", "--url", "http://example.com", "--size", "123x")},
          
          {"Missing value for url",
              args("run",
                  "--url", 
                  "--javascript", "some.js", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")},
                  
          {"Missing value for javascript",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")},
                  
          {"Missing value for include",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")},
                 
          {"Missing value for exclude",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")},
                  
          {"Missing value for size",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", "nomobile", 
                  "--size", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")},
                  
          {"Missing value for spec",
                  args("run",
                      "--url", "http://example.com", 
                      "--javascript", "script.js", 
                      "--include", "mobile", 
                      "--exclude", "nomobile", 
                      "--size", "540x350", 
                      "--spec",
                      "--htmlreport", "some.html")},
                      
          {"Missing value for htmlreport",
                  args("run",
                      "--url", "http://example.com", 
                      "--javascript", "script.js", 
                      "--include", "mobile", 
                      "--exclude", "nomobile", 
                      "--size", "540x350", 
                      "--spec", "page.spec",
                      "--htmlreport")}

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
