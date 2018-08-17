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

import com.galenframework.specs.Alignment;
import com.galenframework.specs.SpecVertically;
import com.galenframework.parser.Expectations;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecHorizontally;
import com.galenframework.parser.StringCharReader;

public class SpecAlignedProcessor implements SpecProcessor {


    @Override
    public Spec process(StringCharReader reader, String contextPath) {
        String direction = reader.readWord();
        boolean vertically;

        if (direction.equals("vertically")) {
            vertically = true;
        } else if (direction.equals("horizontally")) {
            vertically = false;
        } else {
            throw new SyntaxException("Incorrect alignment direction. Expected 'vertically' or 'horizontally' but got: " + direction);
        }

        String side = reader.readWord();
        Alignment alignment = Alignment.parse(side);

        String objectName = reader.readWord();
        if (objectName.isEmpty()) {
            throw new SyntaxException(MISSING_OBJECT_NAME);
        }

        int errorRate = 0;
        if (reader.hasMore()) {
            errorRate = Expectations.errorRate().read(reader);
        }

        if (vertically) {
            return createVerticalSpec(objectName, alignment, errorRate);
        } else {
            return createHorizontalSpec(objectName, alignment, errorRate);
        }

    }

    private Spec createVerticalSpec(String objectName, Alignment alignment, int errorRate) {
        if (!alignment.isOneOf(Alignment.CENTERED, Alignment.ALL, Alignment.LEFT, Alignment.RIGHT)) {
            throw new SyntaxException("Incorrect side for vertical alignment: " + alignment.toString());
        }

        SpecVertically spec = new SpecVertically(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }

    private Spec createHorizontalSpec(String objectName, Alignment alignment, int errorRate) {
        if (!alignment.isOneOf(Alignment.CENTERED, Alignment.ALL, Alignment.TOP, Alignment.BOTTOM)) {
            throw new SyntaxException("Incorrect side for horizontal alignment: " + alignment.toString());
        }

        SpecHorizontally spec = new SpecHorizontally(alignment, objectName);
        spec.setErrorRate(errorRate);
        return spec;
    }
}
