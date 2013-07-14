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
