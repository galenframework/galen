/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.specs.reader.StringCharReader;

import static java.lang.String.format;
import static net.mindengine.galen.parser.Expectations.isDelimeter;
import static net.mindengine.galen.parser.Expectations.isNumeric;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

public class ExpectNumber implements Expectation<Double> {

    @Override
    public Double read(final StringCharReader reader) {
        boolean started = false;
        char symbol;
        boolean hadPointAlready = false;
        final StringBuilder builder = new StringBuilder();
        while (reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            } else if (symbol == '.') {
                if (hadPointAlready) {
                    throw new SyntaxException(UNKNOWN_LINE, String.format("Cannot parse number: \"%s\"", symbol));
                }
                hadPointAlready = true;
                builder.append(symbol);
            } else if (isNumeric(symbol)) {
                builder.append(symbol);
                started = true;
            } else if (started) {
                reader.back();
                break;
            }
        }
        final String doubleText = builder.toString();

        try {
            return Double.parseDouble(doubleText);
        } catch (final Exception e) {
            throw new SyntaxException(UNKNOWN_LINE, format("Cannot parse number: \"%s\"", doubleText), e);
        }
    }
}
