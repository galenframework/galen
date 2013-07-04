package net.mindengine.galen.tests.specs.reader;

import static net.mindengine.galen.specs.Location.BOTTOM;
import static net.mindengine.galen.specs.Location.LEFT;
import static net.mindengine.galen.specs.Location.RIGHT;
import static net.mindengine.galen.specs.Location.TOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.reader.ExpectLocation;
import net.mindengine.galen.specs.reader.ExpectRange;
import net.mindengine.galen.specs.reader.ExpectWord;
import net.mindengine.galen.specs.reader.StringCharReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hamcrest.Matchers;
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
           row("10 to 15px", new Range(10, 15)),
           row("10to15px", new Range(10, 15)),
           row("-10to-15px", new Range(-15, -10)),
           row("10to15 px", new Range(10, 15)),
           row("9 px", new Range(9, null)),
           row("9px", new Range(9, null)),
           row("   9px", new Range(9, null)),
           row("\t9px", new Range(9, null)),
           row("\t9\t\tpx", new Range(9, null)),
           row("-49px", new Range(-49, null)),
           row("15 ± 5 px", new Range(10, 20)),
           row("15±5px", new Range(10, 20)),
           row("15±5px", new Range(10, 20))
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
           row("   je ct", "je")
        };
    }
    
    @Test(dataProvider = "locationTestData")
    public void expectLocations(TestData<List<Location>> testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        List<Location> locations = new ExpectLocation().read(stringCharReader);
        
        Location[] expected = testData.expected.toArray(new Location[testData.expected.size()]);
        assertThat(locations.size(), is(expected.length));
        assertThat(locations, contains(expected));
    }
    
    @DataProvider
    public Object[][] locationTestData() {
        return new Object[][]{
           row("left right", locations(LEFT, RIGHT)),
           row("    \tleft\t  right  ", locations(LEFT, RIGHT)),
           row("   left   ", locations(LEFT)),
           row("top  left   ", locations(TOP, LEFT)),
           row("top  left  bottom ", locations(TOP, LEFT, BOTTOM))
        };
    }
    
    
    
    private List<Location> locations(Location...locations) {
        return Arrays.asList(locations);
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
