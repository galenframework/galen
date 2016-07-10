/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.parser;

import com.galenframework.specs.RangeValue;
import static com.galenframework.parser.Expectations.isDelimeter;
import static com.galenframework.parser.Expectations.isNumeric;

import static java.lang.String.format;

public class ExpectRangeValue implements Expectation<RangeValue> {

    @Override
    public RangeValue read(StringCharReader reader) {
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
                    throw new SyntaxException(format("Cannot parse number: \"%s\"", symbol));
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
            return RangeValue.parseRangeValue(doubleText);
        }
        catch (Exception e) {
            throw new SyntaxException(format("Cannot parse range value: \"%s\"", doubleText), e);
        }
    }
}
