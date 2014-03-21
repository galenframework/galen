package net.mindengine.galen.components;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

    public static Pattern regex (String text){ 
        return Pattern.compile(text);
    }
    public static void assertLines(String text, List<Object> expectedLines) {
        String[] lines = text.split(System.getProperty("line.separator"));
        
        assertThat("Amount of lines should be the same", lines.length, is(expectedLines.size()));
        
        int i=0;
        for (Object expectedLine : expectedLines) {
            if (expectedLine instanceof String) {
                if (!lines[i].equals(expectedLine)) {
                    throw new RuntimeException(String.format("Line %d \n%s\ndoes not equal to:\n%s", i, lines[i], expectedLine));
                }
            }
            else if (expectedLine instanceof Pattern) {
                Matcher m = ((Pattern)expectedLine).matcher(lines[i]);
                if (!m.matches()) {
                    throw new RuntimeException(String.format("Line %d \n%s\ndoesn't match pattern:\n%s", i, lines[i], expectedLine));
                }
            }
            i++;
        }
    }
    public static List<Object> lines(Object ...lines) {
        return Arrays.asList(lines);
    }

}
