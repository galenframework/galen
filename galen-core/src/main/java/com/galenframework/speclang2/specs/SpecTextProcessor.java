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
package com.galenframework.speclang2.specs;

import com.galenframework.parser.Expectations;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecText;
import com.galenframework.parser.StringCharReader;

import java.util.LinkedList;
import java.util.List;

public class SpecTextProcessor implements SpecProcessor {


    @Override
    public Spec process(StringCharReader reader, String contextPath) {

        /* first building up a list of text operations */

        List<String>  textOperations = new LinkedList<String>();

        SpecText.Type textCheckType = null;
        while(textCheckType == null && reader.hasMoreNormalSymbols()) {
            String word = reader.readWord();
            if (word.isEmpty()) {
                throw new SyntaxException("Expected text check type, but got nothing");
            }

            if (SpecText.Type.isValid(word)) {
                textCheckType = SpecText.Type.fromString(word);
            } else {
                textOperations.add(word);
            }
        }

        String expectedText = Expectations.doubleQuotedText().read(reader);

        if (reader.hasMoreNormalSymbols()) {
            throw new SyntaxException("Too many arguments for spec: " + reader.getTheRest().trim());
        }

        return new SpecText(textCheckType, expectedText, textOperations);
    }
}
