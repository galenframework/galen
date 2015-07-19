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
import com.galenframework.specs.reader.StringCharReader;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;


public class ExpectCommaSeparatedKeyValue implements Expectation<List<Pair<String, String>>> {

    @Override
    public List<Pair<String, String>> read(StringCharReader charReader) {
        List<Pair<String, String>> data = new LinkedList<Pair<String, String>>();

        Pair<String, String> currentParam = null;

        while(charReader.hasMore()) {
            if (currentParam == null) {
                String word = new ExpectWord().read(charReader);
                if (!word.isEmpty()) {
                    currentParam = new MutablePair<String, String>(word, "");
                    data.add(currentParam);
                }
            }
            else {
                final String value = charReader.readSafeUntilSymbol(',').trim();
                currentParam.setValue(value);
                currentParam = null;
            }
        }

        return data;
    }
}
