package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.isDelimeter;
import static net.mindengine.galen.specs.reader.Expectations.isNumeric;
import net.mindengine.galen.specs.Range;

public class ExpectRange implements Expectation<Range>{

    @Override
    public Range read(StringCharReader reader) {
        Integer firstValue = expectInt(reader);
        
        String text = expectNonNumeric(reader);
        if (text.equals("px")) {
            return Range.exact(firstValue);
        }
        else if (text.equals("to")) {
            return Range.between(firstValue, readSecondValue(reader));
        }
        else if (text.equals("±")) {
            Integer precision = readSecondValue(reader);
            return Range.between(firstValue - precision, firstValue + precision);
        }
        else {
            throw new IncorrectSpecException("Cannot parse range: \"" + text + "\"");
        }
    }

    private String expectNonNumeric(StringCharReader reader) {
        boolean started = false;
        char symbol;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            }
            else if (isNumeric(symbol)) {
                reader.back();
                break;
            }
            else if (!isDelimeter(symbol)) {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }

    private Integer expectInt(StringCharReader reader) {
        boolean started = false;
        char symbol;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            }
            else if (isNumeric(symbol)) {
                buffer.append(symbol);
                started = true;
            }
            else if (started) {
                reader.back();
                break;
            }
        }
        return Integer.parseInt(buffer.toString());
    }

    

    private Integer readSecondValue(StringCharReader reader) {
        Integer secondValue = expectInt(reader);
        String end = expectNonNumeric(reader);
        if (end.equals("px")) {
            return secondValue;
        }
        else throw new IncorrectSpecException("Cannot parse range");
    }

         
}
