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


public class ExpectWord implements Expectation<String> {

    private char[] delimeters = new char[]{' ', '\t', ','};
    private char[] breakSymbols = null;

    @Override
    public String read(StringCharReader reader) {
        boolean started = false;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            if (isBreaking(symbol)) {
                reader.back();
                break;
            }
            else if(isWordDelimeter(symbol)) {
                if (started) {
                    reader.back();
                    break;
                }
            }
            else {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }

    private boolean isBreaking(char symbol) {
        if (breakSymbols != null) {
            for (char breakSymbol : breakSymbols) {
                if (breakSymbol == symbol) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWordDelimeter(char symbol) {
        for (char delimeter : delimeters) {
            if (symbol == delimeter) {
                return true;
            }
        }
        return false;
    }

    public ExpectWord stopOnTheseSymbols(char...breakSymbols) {
        this.breakSymbols = breakSymbols;
        return this;
    }
    
    public ExpectWord withDelimeters(char...delimeters) {
        this.delimeters = delimeters;
        return this;
    }
    
}
