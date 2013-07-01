package net.mindengine.galen.specs.reader;

import net.mindengine.galen.specs.Range;

public class RangeProcessor {

    public Range process(StringCharReader reader) {
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
            if (started && symbol == ' ') {
                break;
            }
            else if (numeric(symbol)) {
                reader.back();
                break;
            }
            else {
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
            if (started && symbol == ' ') {
                break;
            }
            else if (numeric(symbol)) {
                buffer.append(symbol);
                started = true;
            }
            else {
                reader.back();
                break;
            }
        }
        return Integer.parseInt(buffer.toString());
    }

    private boolean numeric(char symbol) {
        return symbol == '-' || (symbol >= '0' && symbol <= '9');
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
