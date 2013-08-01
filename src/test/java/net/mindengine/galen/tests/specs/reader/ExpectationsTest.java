/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.specs.reader;

import static net.mindengine.galen.specs.Side.BOTTOM;
import static net.mindengine.galen.specs.Side.LEFT;
import static net.mindengine.galen.specs.Side.RIGHT;
import static net.mindengine.galen.specs.Side.TOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.reader.ExpectLocations;
import net.mindengine.galen.specs.reader.ExpectRange;
import net.mindengine.galen.specs.reader.ExpectSides;
import net.mindengine.galen.specs.reader.ExpectWord;
import net.mindengine.galen.specs.reader.IncorrectSpecException;
import net.mindengine.galen.specs.reader.StringCharReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ExpectationsTest {

    @Test(dataProvider = "rangeTestData")
    public void expectRangeTest(TestData<Range> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        Range range = new ExpectRange().read(stringCharReader);
        assertThat(range.getFrom(), is(testData.expected.getFrom()));
        assertThat(range.getTo(), is(testData.expected.getTo()));
    }
    
    
    @DataProvider
    public Object[][] rangeTestData() {
        return new Object[][]{
           row("10 to 15 px", new Range(10, 15)),
           row("10.0 to 15.4 px", new Range(10, 15.4)),
           row("10 to 15px", new Range(10, 15)),
           row("10to15px", new Range(10, 15)),
           row("-10to-15px", new Range(-15, -10)),
           row("-10to-15.04px", new Range(-15.04, -10)),
           row("10to15 px", new Range(10, 15)),
           row("9 px", new Range(9, 9)),
           row("9px", new Range(9, 9)),
           row("9.01px", new Range(9.01, 9.01)),
           row("   9px", new Range(9, 9)),
           row("\t9px", new Range(9, 9)),
           row("\t9\t\tpx", new Range(9, 9)),
           row("-49px", new Range(-49, -49)),
           row("15 ± 5 px", new Range(10, 20)),
           row("15±5px", new Range(10, 20)),
           row("15% of screen/width", new Range(15, 15).withPercentOf("screen/width")),
           row("15.05% of screen/width", new Range(15.05, 15.05).withPercentOf("screen/width")),
           row("15 to 40% of   screen/height", new Range(15, 40).withPercentOf("screen/height")),
           row("15 ± 5 % of   screen/height", new Range(10, 20).withPercentOf("screen/height")),
           row("15 to 40% of item-1/some-other-stuff/a/b/c2", new Range(15, 40).withPercentOf("item-1/some-other-stuff/a/b/c2"))
        };
    }
    
    @Test(dataProvider = "provideBadRangeSamples")
    public void shouldGiveError_forIncorrectRanges(TestData<String> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        
        IncorrectSpecException exception = null;
        try {
            Range range = new ExpectRange().read(stringCharReader);
        }
        catch (IncorrectSpecException e) {
            exception = e;
        }
        
        assertThat("Exception should be", exception, is(notNullValue()));
        assertThat("Exception message should be", exception.getMessage(), is(testData.expected));
    }
    
    @DataProvider
    public Object[][] provideBadRangeSamples() {
        return new Object[][] {
            row("0", "Cannot parse range value: \"\""),
            row("0p", "Cannot parse range value: \"\""),
            row("0 p", "Cannot parse range value: \"\""),
            row("0PX", "Cannot parse range value: \"\""),
            row("10 to 20", "Missing ending: \"px\" or \"%\""),
            row("10 to 20p", "Missing ending: \"px\" or \"%\""),
            row("10 to 20%", "Missing value path for relative range"),
            row("10 to 20% of ", "Missing value path for relative range"),
            row("10% to 20% of ", "Missing value path for relative range"),
            row("15 ± 5", "Missing ending: \"px\" or \"%\""),
            row("15 ± 5 %", "Missing value path for relative range"),
            row("15 ± 5 % of ", "Missing value path for relative range"),
        };
    }
    
    
    @Test(dataProvider = "wordTestData")
    public void expectWord(TestData<String> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        String word = new ExpectWord().read(stringCharReader);
        assertThat(word, is(testData.expected));
    }
    
    @DataProvider
    public Object[][] wordTestData() {
        return new Object[][]{
           row("object", "object"),
           row("  object", "object"),
           row("\tobject ", "object"),
           row("\t\tobject\tanother", "object"),
           row("o ject", "o"),
           row("o123-123124-_124/124|12qw!@#$%^^&*().<>?:\"[]{} ject", "o123-123124-_124/124|12qw!@#$%^^&*().<>?:\"[]{}"),
           row("   je ct", "je")
        };
    }
    
    @Test(dataProvider="wordWithBreakingSymbolTestData")
    public void expectWordWithBreakingSymbol(String text, char breakingSymbol, String expectedWord) {
        StringCharReader stringCharReader = new StringCharReader(text);
        String word = new ExpectWord().stopOnThisSymbol(breakingSymbol).read(stringCharReader);
        assertThat(word, is(expectedWord));
    }
    
    @DataProvider
    public Object[][] wordWithBreakingSymbolTestData() {
        return new Object[][]{
            new Object[]{"Hi, John!", ',', "Hi"},
            new Object[]{" Hi, John!", ',', "Hi"},
            new Object[]{" HiJohn", 'o', "HiJ"},
            new Object[]{"HiJohn", '!', "HiJohn"}
        };
    }
    
    @Test(dataProvider = "sideTestData")
    public void expectSides(TestData<List<Side>> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        List<Side> sides = new ExpectSides().read(stringCharReader);
        
        Side[] expected = testData.expected.toArray(new Side[testData.expected.size()]);
        assertThat(sides.size(), is(expected.length));
        assertThat(sides, contains(expected));
    }
    
    @DataProvider
    public Object[][] sideTestData() {
        return new Object[][]{
           row("left right", sides(LEFT, RIGHT)),
           row("    \tleft\t  right  ", sides(LEFT, RIGHT)),
           row("   left   ", sides(LEFT)),
           row("top  left   ", sides(TOP, LEFT)),
           row("top  left  bottom ", sides(TOP, LEFT, BOTTOM))
        };
    }
    
    @Test(dataProvider = "locationsTestData")
    public void expectLocations(TestData<List<Location>> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        List<Location> sides = new ExpectLocations().read(stringCharReader);
        
        Location[] expected = testData.expected.toArray(new Location[testData.expected.size()]);
        assertThat(sides.size(), is(expected.length));
        assertThat(sides, contains(expected));
    }
    
    @DataProvider
    public Object[][] locationsTestData() {
        return new Object[][]{
           row("10 px left right, 10 to 20 px top bottom", locations(new Location(Range.exact(10), sides(LEFT, RIGHT)), new Location(Range.between(10, 20), sides(TOP, BOTTOM)))),
           row("10 px left, 10 to 20 px top bottom, 30px right", locations(new Location(Range.exact(10), sides(LEFT)), 
                   new Location(Range.between(10, 20), sides(TOP, BOTTOM)), 
                   new Location(Range.exact(30), sides(RIGHT)))),
           row("   10 px left right   ,   10 to 20 px top bottom  ", locations(new Location(Range.exact(10), sides(LEFT, RIGHT)), new Location(Range.between(10, 20), sides(TOP, BOTTOM)))),
           row("\t10 px left right\t,\t10 to 20 px\ttop\tbottom \t \t \t", locations(new Location(Range.exact(10), sides(LEFT, RIGHT)), new Location(Range.between(10, 20), sides(TOP, BOTTOM)))),
        };
    }
    
    @Test(dataProvider = "provideBadLocations")
    public void shouldGiveError_forIncorrectLocations(String text, String expectedErrorMessage) {
        StringCharReader stringCharReader = new StringCharReader(text);
        
        IncorrectSpecException exception = null;
        try {
            new ExpectLocations().read(stringCharReader);
        }
        catch (IncorrectSpecException e) {
            exception = e;
        }
        
        assertThat("Exception should be", exception, is(notNullValue()));
        assertThat("Exception message should be", exception.getMessage(), is(expectedErrorMessage));
    }
    
    @DataProvider
    public Object[][] provideBadLocations() {
        return new Object[][]{
            new Object[]{"left", "Cannot parse range value: \"\""},
            new Object[]{"10px qwe", "Unknown side: \"qwe\""},
            new Object[]{"10 to 30px qwe", "Unknown side: \"qwe\""},
            new Object[]{"10 to 30% of screen/width qwe", "Unknown side: \"qwe\""},
            new Object[]{"10px left qwe", "Unknown side: \"qwe\""},
            new Object[]{"10px left, 20px qwe", "Unknown side: \"qwe\""},
            new Object[]{"10px left, 20px left qwe", "Unknown side: \"qwe\""},
            new Object[]{"10px left, right, top", "Cannot parse range value: \"\""},
        };
    }
    
    
    private List<Location> locations(Location...locations) {
        return Arrays.asList(locations);
    }

    private List<Side> sides(Side...sides) {
        return Arrays.asList(sides);
    }

    private <T> Object[] row(String textForParsing, T expectedRange) {
        return new Object[]{new TestData<T>(textForParsing, expectedRange)};
    }


    private class TestData<T> {
        private String textForParsing;
        private T expected;
        
        public TestData(String textForParsing, T expected) {
            this.textForParsing = textForParsing;
            this.expected = expected;
        }
        
        @Override
        public String toString() {
            return StringEscapeUtils.escapeJava(textForParsing);
        }
    }
}
