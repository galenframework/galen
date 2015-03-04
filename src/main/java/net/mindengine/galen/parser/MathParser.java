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

import net.mindengine.galen.javascript.GalenJsExecutor;

import net.mindengine.galen.specs.reader.StringCharReader;

public class MathParser {

    private final char mathSymbol = '@';

    public String parse(final String template, final String initialValue) {

        final StringCharReader reader = new StringCharReader(template);

        final StringBuilder text = new StringBuilder();

        while (reader.hasMore()) {
            final char ch = reader.next();

            if (ch == mathSymbol) {
                if (reader.hasMore()) {
                    final char nextCh = reader.next();
                    if (nextCh == mathSymbol) {
                        text.append(mathSymbol);
                    } else if (nextCh == '{') {
                        final String expression = reader.readUntilSymbol('}').replace(" ", "");
                        if (expression.length() < 2) {
                            throw new SyntaxException("Can't parse expression: " + expression);
                        }

                        text.append(convertExpression(initialValue, expression));

                    } else {
                        text.append(initialValue);
                        text.append(nextCh);
                    }
                } else {
                    text.append(initialValue);
                }
            } else {
                text.append(ch);
            }
        }

        return text.toString();
    }

    private static final char[] mathOperations = { '+', '-', '/', '*', '%' };

    private String convertExpression(final String initialValue, String expression) {
        final int index = Integer.parseInt(initialValue);

        if (startsWithOneOfTheseSymbols(expression, mathOperations)) {
            expression = "index" + expression;
        }

        return Integer.toString(execJavascript(index, expression));
    }

    private int execJavascript(final int index, final String expression) {
        final GalenJsExecutor jsExecutor = new GalenJsExecutor();
        jsExecutor.putObject("index", index);
        final Number number = (Number) jsExecutor.eval(expression);
        return number.intValue();
    }

    private boolean startsWithOneOfTheseSymbols(final String expression, final char[] mathOperations) {
        if (expression.length() > 0) {
            final char firstSymbol = expression.charAt(0);
            for (final char symbol : mathOperations) {
                if (firstSymbol == symbol) {
                    return true;
                }
            }
        }
        return false;
    }

}
