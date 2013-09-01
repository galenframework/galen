package net.mindengine.galen.parser;

import static net.mindengine.galen.parser.Expectations.isDelimeter;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectRect implements Expectation<Rect> {

    @Override
    public Rect read(StringCharReader reader) {
        boolean started = false;
        StringBuffer numbersText = new StringBuffer();
        
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            if (symbol == '(' && !started) {
                started = true;
            }
            else if (symbol == ')') {
                return processCorrection(numbersText.toString());
            }
            else if (!isDelimeter(symbol)) {
                if (!started) {
                    throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. Missing starting '(' symbol");
                }
                numbersText.append(symbol);
            }
        }
        
        throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. Missing closing ')' symbol");
    }

    private Rect processCorrection(String numbersText) {
        if (!numbersText.isEmpty()) {
            String values[] = numbersText.split(",");
            if (values.length == 4) {
                int [] numbers = convertToNumbers(values);
                return new Rect(numbers[0], numbers[1], numbers[2], numbers[3]);
            }
            else throw new SyntaxException(UNKNOWN_LINE, "Wrong number of arguments in corrections: " + values.length);
        }
        else throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. No values provided");
    }

    private int[] convertToNumbers(String[] values) {
        int[] numbers = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i].startsWith("+")) {
                values[i] = values[i].substring(1);
            }
            numbers[i] = Integer.parseInt(values[i]);
        }
        return numbers;
    }

}
