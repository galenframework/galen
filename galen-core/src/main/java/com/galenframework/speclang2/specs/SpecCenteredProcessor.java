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

import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecCentered;
import com.galenframework.parser.StringCharReader;
import com.galenframework.parser.Expectations;

public class SpecCenteredProcessor implements SpecProcessor {
    @Override
    public Spec process(StringCharReader reader, String contextPath) {

        SpecCentered.Alignment alignment = SpecCentered.Alignment.ALL;
        SpecCentered.Location location = null;

        String word  = reader.readWord();

        if (word.isEmpty()) {
            throw new SyntaxException("Missing location and alignment");
        }


        if (SpecCentered.Location.isValid(word)) {
            location = SpecCentered.Location.fromString(word);

        } else {
            alignment = SpecCentered.Alignment.fromString(word);

            String locationWord = reader.readWord();
            if (locationWord.isEmpty()) {
                throw new SyntaxException("Missing location (on, inside)");
            }
            location = SpecCentered.Location.fromString(locationWord);
        }


        String object = reader.readWord();
        if (object.isEmpty()) {
            throw new SyntaxException("Missing object name");
        }

        int errorRate = 2;
        if (reader.hasMore()) {
            errorRate = Expectations.errorRate().read(reader);
        }


        return new SpecCentered(object, alignment, location).withErrorRate(errorRate);

    }
}
