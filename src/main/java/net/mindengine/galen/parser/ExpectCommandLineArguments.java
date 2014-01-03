/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectCommandLineArguments implements Expectation<String[]> {

    @Override
    public String[] read(StringCharReader reader) {
        
        ExpectWord expectWord = new ExpectWord().withDelimeters(' ').stopOnTheseSymbols('"', '\'');
        ExpectString expectString = new ExpectString();
        
        
        List<String> arguments = new LinkedList<String>();
        
        while(reader.hasMore()) {
            String word = expectWord.read(reader);
            
            if (!word.isEmpty()) {
                arguments.add(word);
            }
            
            
            if (reader.currentSymbol() == '"' || reader.currentSymbol() == '\'') {
                expectString.setQuotesSymbol(reader.currentSymbol());
                if (reader.hasMore()) {
                    reader.next();
                    String string = expectString.read(reader);
                    arguments.add(string);
                }
            }
        }
        
        
        return arguments.toArray(new String[]{});
    }

}
