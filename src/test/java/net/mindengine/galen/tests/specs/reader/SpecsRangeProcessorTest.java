package net.mindengine.galen.tests.specs.reader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.reader.RangeProcessor;
import net.mindengine.galen.specs.reader.StringCharReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SpecsRangeProcessorTest {

    @Test(dataProvider = "testData")
    public void readsData(TestData testData) {
        StringCharReader stringCharReader = new StringCharReader(testData.textForParsing);
        Range range = new RangeProcessor().process(stringCharReader);
        assertThat(range.getFrom(), is(testData.expectedRange.getFrom()));
        assertThat(range.getTo(), is(testData.expectedRange.getTo()));
    }
    
    @DataProvider
    public Object[][] testData() {
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
    
    
    private Object[] row(String textForParsing, Range expectedRange) {
        return new Object[]{new TestData(textForParsing, expectedRange)};
    }


    private class TestData {
        private String textForParsing;
        private Range expectedRange;
        
        public TestData(String textForParsing, Range expectedRange) {
            this.textForParsing = textForParsing;
            this.expectedRange = expectedRange;
        }
        
        @Override
        public String toString() {
            return StringEscapeUtils.escapeJava(textForParsing);
        }
    }
}
