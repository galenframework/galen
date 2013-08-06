package net.mindengine.galen.tests.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.Dimension;

import net.mindengine.galen.runner.GalenArguments;

import org.apache.commons.cli.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArgumentParserTest {

    @Test(dataProvider = "provideGoodSamples")
    public void shoulParseArguments(SimpleArguments args,  GalenArguments expectedArguments) throws ParseException {
        GalenArguments realArguments = GalenArguments.parse(args.args);
        assertThat(realArguments, is(expectedArguments));
    }
    
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            row(args("run", "--url", "http://mindengine.net", 
                            "--javascript", "some.js", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--spec", "some.spec",
                            "--htmlreport", "some.html"), 
                new GalenArguments()
                    .withAction("run")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")),

            row(args("run", "-u", "http://mindengine.net", 
                            "-j", "some.js", 
                            "-i", "mobile,all", 
                            "-e", "nomobile,testTag", 
                            "-r", "400x700", 
                            "-s", "some.spec",
                            "-h", "some.html"), 
                
            new GalenArguments()
                    .withAction("run")
                    .withUrl("http://mindengine.net")
                    .withJavascript("some.js")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")),

            row(args("run", "--url", "http://mindengine.net", 
                            "--include", "mobile,all", 
                            "--exclude", "nomobile,testTag", 
                            "--size", "400x700", 
                            "--spec", "some.spec",
                            "--htmlreport", "some.html"), 
                new GalenArguments()
                    .withAction("run")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags("mobile", "all")
                    .withExcludedTags("nomobile", "testTag")
                    .withScreenSize(new Dimension(400, 700))
                    .withSpec("some.spec")
                    .withHtmlReport("some.html")),
                   
            row(args("run", "--url", "http://mindengine.net", 
                            "--spec", "some.spec"), 
                new GalenArguments()
                    .withAction("run")
                    .withUrl("http://mindengine.net")
                    .withIncludedTags()
                    .withExcludedTags()
                    .withSpec("some.spec")),
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
          row("Unknown action: do", 
              args("do", "--url", "http://example.com")),
          
          row("Incorrect size: 123", 
              args("run", "--url", "http://example.com", "--size", "123")),
          
          row("Incorrect size: 123xx123", 
              args("run", "--url", "http://example.com", "--size", "123xx123")),
          
          row("Incorrect size: a123xx123", 
              args("run", "--url", "http://example.com", "--size", "a123xx123")),
          
          row("Incorrect size: 123x", 
              args("run", "--url", "http://example.com", "--size", "123x")),
          
          row("Missing url",
              args("run",  
                  "--javascript", "some.js", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
          
          row("Missing spec file",
              args("run",
                  "--url", "http://example.com",
                  "--javascript", "some.js", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--htmlreport", "some.html")),
          
          row("Missing value for url",
              args("run",
                  "--url", 
                  "--javascript", "some.js", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
                  
          row("Missing value for javascript",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", 
                  "--include", "mobile,all", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
                  
          row("Missing value for include",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", 
                  "--exclude", "nomobile,testTag", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
                 
          row("Missing value for exclude",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", 
                  "--size", "400x700", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
                  
          row("Missing value for size",
              args("run",
                  "--url", "http://example.com", 
                  "--javascript", "script.js", 
                  "--include", "mobile", 
                  "--exclude", "nomobile", 
                  "--size", 
                  "--spec", "some.spec",
                  "--htmlreport", "some.html")),
                  
          row("Missing value for spec",
                  args("run",
                      "--url", "http://example.com", 
                      "--javascript", "script.js", 
                      "--include", "mobile", 
                      "--exclude", "nomobile", 
                      "--size", "540x350", 
                      "--spec",
                      "--htmlreport", "some.html")),
                      
          row("Missing value for htmlreport",
                  args("run",
                      "--url", "http://example.com", 
                      "--javascript", "script.js", 
                      "--include", "mobile", 
                      "--exclude", "nomobile", 
                      "--size", "540x350", 
                      "--spec", "page.spec",
                      "--htmlreport")),

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


    private Object[] row(Object...args) {
        return args;
    }
}
