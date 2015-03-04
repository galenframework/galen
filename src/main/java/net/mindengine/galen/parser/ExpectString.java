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

public class ExpectString implements Expectation<String> {

    private char quotesSymbol = '"';

    @Override
    public String read(final StringCharReader reader) {
        final StringBuilder builder = new StringBuilder();
        while (reader.hasMore()) {
            final char symbol = reader.next();

            if (symbol == quotesSymbol) {
                break;
            } else if (symbol == '\\') {
                if (reader.hasMore()) {
                    builder.append(asEscapeSymbol(reader.next()));
                } else {
                    builder.append("\\");
                    break;
                }
            } else {
                builder.append(symbol);
            }
        }
        return builder.toString();
    }

    private char asEscapeSymbol(final char symbol) {
        if (symbol == 'n') {
            return '\n';
        }
        if (symbol == 't') {
            return '\t';
        }
        if (symbol == 'b') {
            return '\b';
        }
        if (symbol == 'r') {
            return '\r';
        }
        if (symbol == 'f') {
            return '\f';
        } else {
            return symbol;
        }
    }

    public ExpectString setQuotesSymbol(final char symbol) {
        this.quotesSymbol = symbol;
        return this;
    }

}
