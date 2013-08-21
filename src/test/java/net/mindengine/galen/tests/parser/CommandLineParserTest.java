package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.CommandLineParser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CommandLineParserTest {
    
    
    @Test(dataProvider="provideCommandLineSamples") public void shouldParse_commandLineToArguments(String text, String[] expectedArguments) {
        String[] arguments = CommandLineParser.parseCommandLine(text);
        assertThat(arguments, is(expectedArguments));
    }
    
    
    @DataProvider public Object[][] provideCommandLineSamples() {
        return new Object[][]{
            test("script -v 1 --data qwer --data \"\" -\\ \"qw er rt \"", args("script", "-v", "1", "--data", "qwer", "--data", "", "-\\", "qw er rt ")),
            test("script -v 1 --data qwer --data '' -\\ 'qw er rt '", args("script", "-v", "1", "--data", "qwer", "--data", "", "-\\", "qw er rt ")),
            test("    script     -v  1    ", args("script", "-v", "1")),
            test("\"hello\" world", args("hello", "world")),
            test("script \"\\\"Hi \\\\ \\\"\"", args("script", "\"Hi \\ \"")),
            test("script '\\\'Hi \\\\ \\\''", args("script", "'Hi \\ '")),
            test("\"hello\\", args("hello\\")),
            test("\"hello  ' world\"", args("hello  ' world")),
            test("'hello  \" world'", args("hello  \" world")),
            test("'hello  \"'  'world'", args("hello  \"", "world"))
            
        };
    }


    private Object[] test(Object...args) {
        return args;
    }


    private String[] args(String...args) {
        return args;
    }
    
}
