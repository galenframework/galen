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
package net.mindengine.galen.parser;

import static java.lang.String.format;
import static net.mindengine.galen.parser.Expectations.isDelimeter;
import static net.mindengine.galen.parser.Expectations.isNumeric;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectRange implements Expectation<Range>{

    @Override
    public Range read(StringCharReader reader) {
        
        boolean approximate = false;
        if (reader.firstNonWhiteSpaceSymbol() == '~') {
            approximate = true;
        }
        
        
        Double firstValue = expectDouble(reader);
        
        String text = expectNonNumeric(reader);
        if (text.equals("%")) {
            return createRange(firstValue, approximate).withPercentOf(readPercentageOf(reader));
        }
        if (text.equals("px")) {
            return createRange(firstValue, approximate);
        }
        else if (!approximate){
            Double secondValue = expectDouble(reader);
            
            Range range = null;
            if (text.equals("to")) {
                range = Range.between(firstValue, secondValue);
            }
            else if (isPlusMinus(text)) {
                range = Range.between(firstValue - secondValue, firstValue + secondValue);
            }
            else {
                throw new SyntaxException(UNKNOWN_LINE, msgFor(text));
            }
            
            String end = expectNonNumeric(reader);
            if (end.equals("px")) {
                return range;
            }
            else if (end.equals("%")) {
                return range.withPercentOf(readPercentageOf(reader));
            }
            else throw new SyntaxException(UNKNOWN_LINE, "Missing ending: \"px\" or \"%\"");
        }
        else throw new SyntaxException(UNKNOWN_LINE, msgFor(text));
    }

    private boolean isPlusMinus(String text) {
        if (text.equals("±")) {
            return true;
        }
        else if (text.length() == 2) {
            int code = (int)text.charAt(0) * 1000 + (int)text.charAt(1);
            return code == 172177; //This is '±' (plus-minus) symbol in different encoding
        }
        else {
            return false;
        }
    }

    private Range createRange(Double firstValue, boolean approximate) {
        if (approximate) {
            
            Double delta = Math.abs(firstValue) / 100;
            if (delta < 1.0) {
                delta = 1.0;
            }
            
            return Range.between(firstValue - delta, firstValue + delta);
        }
        else {
            return Range.exact(firstValue);
        }
    }

    private String readPercentageOf(StringCharReader reader) {
        String firstWord = expectNonNumeric(reader);
        if (firstWord.equals("of")) {
            String valuePath = expectNonNumeric(reader).trim();
            if (valuePath.isEmpty()) {
                throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
            }
            else return valuePath;
        }
        else throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
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

    private Double expectDouble(StringCharReader reader) {
        boolean started = false;
        char symbol;
        boolean hadPointAlready = false;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            }
            else if (symbol == '.') {
                if (hadPointAlready) {
                    throw new SyntaxException(UNKNOWN_LINE, msgFor("" + symbol)); 
                }
                hadPointAlready = true;
                buffer.append(symbol);
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
        String doubleText = buffer.toString();
        
        try {
            return Double.parseDouble(doubleText);
        }
        catch (Exception e) {
            throw new SyntaxException(UNKNOWN_LINE, format("Cannot parse range value: \"%s\"", doubleText), e);
        }
    }

    private String msgFor(String text) {
        return String.format("Cannot parse range: \"%s\"", text);
    }
         
}
