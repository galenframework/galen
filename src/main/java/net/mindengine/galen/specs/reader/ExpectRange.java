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
package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.isDelimeter;
import static net.mindengine.galen.specs.reader.Expectations.isNumeric;
import net.mindengine.galen.specs.Range;

public class ExpectRange implements Expectation<Range>{

    @Override
    public Range read(StringCharReader reader) {
        Double firstValue = expectDouble(reader);
        
        String text = expectNonNumeric(reader);
        if (text.equals("%")) {
            return Range.exact(firstValue).withPercentOf(readPercentageOf(reader));
        }
        if (text.equals("px")) {
            return Range.exact(firstValue);
        }
        else {
            Double secondValue = expectDouble(reader);
            
            Range range = null;
            if (text.equals("to")) {
                range = Range.between(firstValue, secondValue);
            }
            else if (text.equals("±")) {
                return Range.between(firstValue - secondValue, firstValue + secondValue);
            }
            else throw new IncorrectSpecException(msgFor(text));
            
            String end = expectNonNumeric(reader);
            if (end.equals("px")) {
                return range;
            }
            else if (end.equals("%")) {
                return range.withPercentOf(readPercentageOf(reader));
            }
            else throw new IncorrectSpecException(msgFor(text));
        }
    }

    private String readPercentageOf(StringCharReader reader) {
        String firstWord = expectNonNumeric(reader);
        if (firstWord.equals("of")) {
            return expectNonNumeric(reader);
        }
        else throw new IncorrectSpecException(msgFor(firstWord));
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
                    throw new IncorrectSpecException(msgFor("" + symbol)); 
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
        return Double.parseDouble(buffer.toString());
    }

    private String msgFor(String text) {
        return String.format("Cannot parse range: \"%s\"", text);
    }
         
}
