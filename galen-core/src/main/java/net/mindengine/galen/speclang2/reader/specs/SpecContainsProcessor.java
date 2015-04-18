/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.speclang2.reader.specs;

import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.reader.StringCharReader;

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

        List<String> objectNames = Expectations.readAllWords(reader.getTheRest());

        if (objectNames.size() == 0) {
            throw new SyntaxException(MISSING_OBJECT_NAME);
        }

        return new SpecContains(objectNames, partly);
    }
}
