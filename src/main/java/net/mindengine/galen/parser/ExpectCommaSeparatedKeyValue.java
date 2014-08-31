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

import net.mindengine.galen.specs.reader.StringCharReader;

import java.util.HashMap;
import java.util.Map;


public class ExpectCommaSeparatedKeyValue implements Expectation<Map<String, String>> {

    @Override
    public Map<String, String> read(StringCharReader charReader) {
        Map<String, String> data = new HashMap<String, String>();


        String currentParamName = null;
        while(charReader.hasMore()) {
            if (currentParamName == null) {
                String word = new ExpectWord().read(charReader);
                if (!word.isEmpty()) {
                    currentParamName = word;
                }
            }
            else {
                String value = charReader.readUntilSymbol(',').trim();
                data.put(currentParamName, value);
                currentParamName = null;
            }
        }

        return data;
    }
}
