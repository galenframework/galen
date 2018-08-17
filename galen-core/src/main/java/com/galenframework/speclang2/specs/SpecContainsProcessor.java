/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
import com.galenframework.specs.SpecContains;
import com.galenframework.parser.StringCharReader;

import java.util.List;

public class SpecContainsProcessor implements SpecProcessor {

    @Override
    public Spec process(StringCharReader reader, String contextPath) {
        boolean partly = false;

        int initialCursorPosition = reader.currentCursorPosition();

        if (reader.readWord().equals("partly")) {
            partly = true;
        } else {
            reader.moveCursorTo(initialCursorPosition);
        }

        List<String> objectNames = Expectations.readAllWords(reader.takeTheRest());

        if (objectNames.size() == 0) {
            throw new SyntaxException(MISSING_OBJECT_NAME);
        }

        return new SpecContains(objectNames, partly);
    }
}
