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

import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.suite.reader.Context;

public class BashTemplate {

    private static final int PARSING_TEXT = 0;
    private static final int PARSING_PARAM = 1;

    private String templateText;
    
    private int state = PARSING_TEXT;

    public BashTemplate(String templateText) {
        this.templateText = templateText;
    }

    public String process(Context context) {
        StringCharReader reader = new StringCharReader(templateText);
        
        StringBuffer buffer = new StringBuffer();
        
        StringBuffer currentParam = new StringBuffer();
        
        while(reader.hasMore()) {
            char symbol = reader.next();
            if (state ==  PARSING_TEXT) {
                if (symbol == '$' && reader.currentSymbol() == '{') {
                    state = PARSING_PARAM;
                    currentParam = new StringBuffer();
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
                    String paramName = currentParam.toString().trim();
                    Object value = context.getValue(paramName);
                    if (value == null) {
                        //Looking for value in system properties
                        value = System.getProperty(paramName, "");
                    }
                    buffer.append(value.toString());
                    state = PARSING_TEXT;
                }
                else {
                    currentParam.append(symbol);
                }
                
            }
        }
        return buffer.toString();
    }

}
