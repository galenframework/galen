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

import static com.galenframework.parser.Expectations.isDelimeter;
import static com.galenframework.suite.reader.Line.UNKNOWN_LINE;

import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.page.CorrectionsRect.Correction;
import com.galenframework.specs.reader.StringCharReader;

public class ExpectCorrection implements Expectation<CorrectionsRect> {

    @Override
    public CorrectionsRect read(StringCharReader reader) {
        boolean started = false;
        StringBuffer numbersText = new StringBuffer();
        
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            if (symbol == '(' && !started) {
                started = true;
            }
            else if (symbol == ')') {
                return processCorrection(numbersText.toString());
            }
            else if (!isDelimeter(symbol)) {
                if (!started) {
                    throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. Missing starting '(' symbol");
                }
                numbersText.append(symbol);
            }
        }
        
        throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. Missing closing ')' symbol");
    }

    private CorrectionsRect processCorrection(String numbersText) {
        if (!numbersText.isEmpty()) {
            String values[] = numbersText.split(",");
            if (values.length == 4) {
                return new CorrectionsRect(readCorrection(values[0]), readCorrection(values[1]), readCorrection(values[2]), readCorrection(values[3]));
            }
            else throw new SyntaxException(UNKNOWN_LINE, "Wrong number of arguments in corrections: " + values.length);
        }
        else throw new SyntaxException(UNKNOWN_LINE, "Error parsing corrections. No values provided");
    }

    private CorrectionsRect.Correction readCorrection(String value) {
        if (value.length() == 0) {
            throw new SyntaxException("Incorrect correction. Don't use empty values");
        }
        
        char symbol = value.charAt(0);
        
        if (symbol == '-') {
            return new CorrectionsRect.Correction(Integer.parseInt(value.substring(1)), CorrectionsRect.Type.MINUS);
        }
        else if (symbol == '+') {
            return new CorrectionsRect.Correction(Integer.parseInt(value.substring(1)), CorrectionsRect.Type.PLUS);
        }
        if (symbol == '=') {
            return new CorrectionsRect.Correction(Integer.parseInt(value.substring(1)), CorrectionsRect.Type.EQUALS);
        }
        else {
            return new CorrectionsRect.Correction(Integer.parseInt(value), CorrectionsRect.Type.PLUS);
        }
    }

}
