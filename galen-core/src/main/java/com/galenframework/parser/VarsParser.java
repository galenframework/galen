/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.suite.reader.Context;

import java.util.Properties;

public class VarsParser {

    private static final int PARSING_TEXT = 0;
    private static final int PARSING_PARAM = 1;
    private final VarsParserJsProcessable jsProcessor;

    private Context context;

    private int state = PARSING_TEXT;
    private Properties properties;
   

    public VarsParser(Context context, Properties properties, VarsParserJsProcessable jsProcessor) {
        this.context = context;
        this.properties = properties;
        this.jsProcessor = jsProcessor;
    }

    public VarsParser(Context context, Properties properties) {
        this(context, properties, null);
    }

    public String parseStrict(String templateText) {
        return parse(templateText, true);
    }

    public String parse(String templateText) {
        return parse(templateText, false);
    }

    private String parse(String templateText, boolean strict) {
        StringCharReader reader = new StringCharReader(templateText);
        
        StringBuffer buffer = new StringBuffer();
        
        StringBuffer currentExpression = new StringBuffer();
        
        while(reader.hasMore()) {
            char symbol = reader.next();
            if (state ==  PARSING_TEXT) {
                if (symbol == '$' && reader.currentSymbol() == '{') {
                    state = PARSING_PARAM;
                    currentExpression = new StringBuffer();
                    reader.next();
                }
                else if(symbol=='\\' && reader.currentSymbol() == '$') {
                    buffer.append('$');
                    reader.next();
                }
                else {
                    buffer.append(symbol);
                }
            }
            else if (state ==  PARSING_PARAM) {
                if (symbol == '}') {
                    String expression = currentExpression.toString().trim();
                    Object value = getExpressionValue(expression, context, strict);
                    if (value == null) {
                        value = "";
                    }
                    buffer.append(value.toString());
                    state = PARSING_TEXT;
                }
                else {
                    currentExpression.append(symbol);
                }
                
            }
        }
        return buffer.toString();
    }

    
    private Object getExpressionValue(String expression, Context context, boolean strict) {
        Object value = context.getValue(expression);
        if (value == null) {
            //Looking for value in properties

            if (properties != null) {
                value = properties.getProperty(expression);
            }

            if (value == null) {
                value = System.getProperty(expression);
            }
        }
        if (value == null){
            value = readJsExpression(expression, context, strict);
        }

        if (value == null) {
            return "";
        }
        else return value;
    }

    private String readJsExpression(String expression, Context context, boolean strict) {
        if (jsProcessor != null) {
            if (strict) {
                return jsProcessor.evalStrictToString(expression);
            } else {
                return jsProcessor.evalSafeToString(expression);
            }
        }
        else return null;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
