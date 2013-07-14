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
package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.isWordDelimeter;


public class ExpectWord implements Expectation<String> {

    private char breakSymbol = 0;

    @Override
    public String read(StringCharReader reader) {
        boolean started = false;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            
            if (breakSymbol != 0 && !started && symbol == breakSymbol) {
                reader.back();
                return "";
            }
            
            if(isWordDelimeter(symbol)) {
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

    public ExpectWord stopOnThisSymbol(char breakSymbol) {
        this.breakSymbol = breakSymbol;
        return this;
    }
    
}
