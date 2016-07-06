/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.Spec;
import com.galenframework.parser.StringCharReader;
import com.galenframework.parser.Expectations;
import com.galenframework.specs.Range;

public abstract class SpecWithObjectAndRangeProcessor implements SpecProcessor {

    public abstract Spec createSpec(String objectName, Range range);

    @Override
    public Spec process(StringCharReader reader, String contextPath) {

        String objectName = reader.readWord();
        if (objectName.isEmpty()) {
            throw new SyntaxException("Missing object name");
        }

        Range range;
        if (reader.hasMore()) {
            range = Expectations.range().read(reader);
        }
        else {
            range = Range.greaterThanOrEquals(new RangeValue(0));
        }


        return createSpec(objectName, range);
    }
}
