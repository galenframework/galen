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

import java.util.Properties;

import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.suite.reader.Context;

public class VarsParser {

    private static final int PARSING_TEXT = 0;
    private static final int PARSING_PARAM = 1;
    private final VarsParserJsProcessor jsProcessor;

    private final Context context;

    private int state = PARSING_TEXT;
    private Properties properties;

    public VarsParser(final Context context, final Properties properties, final VarsParserJsProcessor jsProcessor) {
        this.context = context;
        this.properties = properties;
        this.jsProcessor = jsProcessor;
    }

    public VarsParser(final VarsContext varsContext, final Properties properties) {
        this(varsContext, properties, null);
    }

    public String parse(final String templateText) {
        final StringCharReader reader = new StringCharReader(templateText);

        final StringBuilder builder = new StringBuilder();

        StringBuilder currentExpression = new StringBuilder();

        while (reader.hasMore()) {
            final char symbol = reader.next();
            if (state == PARSING_TEXT) {
                if (symbol == '$' && reader.currentSymbol() == '{') {
                    state = PARSING_PARAM;
                    currentExpression = new StringBuilder();
                    reader.next();
                } else if (symbol == '\\' && reader.currentSymbol() == '$') {
                    builder.append('$');
                    reader.next();
                } else {
                    builder.append(symbol);
                }
            } else if (state == PARSING_PARAM) {
                if (symbol == '}') {
                    final String expression = currentExpression.toString().trim();
                    Object value = getExpressionValue(expression, context);
                    if (value == null) {
                        value = "";
                    }
                    builder.append(value.toString());
                    state = PARSING_TEXT;
                } else {
                    currentExpression.append(symbol);
                }

            }
        }
        return builder.toString();
    }

    private Object getExpressionValue(final String expression, final Context context) {
        Object value = context.getValue(expression);
        if (value == null) {
            // Looking for value in properties

            if (properties != null) {
                value = properties.getProperty(expression);
            }

            if (value == null) {
                value = System.getProperty(expression);
            }
        }
        if (value == null) {
            value = readJsExpression(expression, context);
        }

        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    private String readJsExpression(final String expression, final Context context) {
        if (jsProcessor != null) {
            return jsProcessor.process(expression);
        } else {
            return null;
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

}
