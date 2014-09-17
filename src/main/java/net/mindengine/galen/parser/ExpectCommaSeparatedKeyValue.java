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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ExpectCommaSeparatedKeyValue implements Expectation<Map<String, List<String>>> {

    @Override
    public Map<String, List<String>> read(StringCharReader charReader) {
        Map<String, List<String>> data = new HashMap<String, List<String>>();


        String currentParamName = null;
        while(charReader.hasMore()) {
            if (currentParamName == null) {
                String word = new ExpectWord().read(charReader);
                if (!word.isEmpty()) {
                    currentParamName = word;
                    if (!data.containsKey(currentParamName)) {
                        data.put(currentParamName, new LinkedList<String>());
                    }
                }
            }
            else {
                final String value = charReader.readUntilSymbol(',').trim();
                data.get(currentParamName).add(value);

                currentParamName = null;
            }
        }

        return data;
    }
}
