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
