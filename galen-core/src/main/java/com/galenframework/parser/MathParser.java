/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import com.galenframework.javascript.GalenJsExecutor;

public class MathParser {
	
	private char mathSymbol = '@';

	public String parse(String template, String initialValue) {
		
		StringCharReader reader = new StringCharReader(template);
		
		StringBuffer text = new StringBuffer();
		
		while(reader.hasMore()) {
			char ch = reader.next();
			
			if (ch == mathSymbol) {
			    if (reader.hasMore()) {
			        char nextCh = reader.next();
	                if (nextCh == mathSymbol) {
	                    text.append(mathSymbol);
	                }
	                else if (nextCh == '{') {
	                     String expression = reader.readSafeUntilSymbol('}').replace(" ", "");
	                     if (expression.length() < 2) {
	                         throw new SyntaxException("Can't parse expression: " + expression);
	                     }
	                     
	                     text.append(convertExpression(initialValue, expression));
	                     
	                }
	                else {
	                    text.append(initialValue);
	                    text.append(nextCh);
	                }
			    }
			    else {
			        text.append(initialValue);   
			    }
			}
			else {
				text.append(ch);
			}
		}
		
		return text.toString();
	}

    private static final char[] mathOperations = {'+', '-', '/', '*','%'};

	private String convertExpression(String initialValue, String expression) {
        int index = Integer.parseInt(initialValue);

        if (startsWithOneOfTheseSymbols(expression, mathOperations)) {
            expression = "index" + expression;
        }

        return Integer.toString(execJavascript(index, expression));
	}

    private int execJavascript(int index, String expression) {
        GalenJsExecutor jsExecutor = new GalenJsExecutor();
        jsExecutor.putObject("index", index);
        Number number = (Number)jsExecutor.eval(expression);
        return number.intValue();
    }

    private boolean startsWithOneOfTheseSymbols(String expression, char[] mathOperations) {
        if (expression.length() > 0) {
            char firstSymbol = expression.charAt(0);
            for (char symbol: mathOperations) {
                if (firstSymbol == symbol) {
                    return true;
                }
            }
        }
        return false;
    }

}
