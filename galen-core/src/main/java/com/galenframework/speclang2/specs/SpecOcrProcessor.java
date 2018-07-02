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
package com.galenframework.speclang2.specs;

import java.util.LinkedList;
import java.util.List;

import com.galenframework.parser.Expectations;
import com.galenframework.parser.StringCharReader;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecOcr;
import com.galenframework.specs.SpecOcr.Type;

public class SpecOcrProcessor implements SpecProcessor {


    @Override
    public Spec process(StringCharReader reader, String contextPath) {

        /* first building up a list of text operations */

        List<String>  textOperations = new LinkedList<>();

        SpecOcr.Type textCheckType = null;
        boolean isDom = false;
        while(textCheckType == null && reader.hasMoreNormalSymbols()) {
            String word = reader.readWord();
            if (word.isEmpty()) {
                throw new SyntaxException("Expected text check type, but got nothing");
            }

            if (SpecOcr.Type.isValid(word)) {
                textCheckType = SpecOcr.Type.fromString(word);
                if(textCheckType == Type.DOMIS || textCheckType == Type.DOM_STARTS) {
                	isDom = true;
                }
            } else {
                textOperations.add(word);
            }
        }
        String expectedText = "";
        if(!isDom) {
        	expectedText = Expectations.doubleQuotedText().read(reader);
    	}

        if (reader.hasMoreNormalSymbols()) {
            throw new SyntaxException("Too many arguments for spec: " + reader.getTheRest().trim());
        }

        return new SpecOcr(textCheckType, expectedText, textOperations);
    }
}
