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
import com.galenframework.specs.Location;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecNear;
import com.galenframework.parser.StringCharReader;

import java.util.List;

public class SpecNearProcessor implements SpecProcessor {


    @Override
    public Spec process(StringCharReader reader, String contextPath) {
        String objectName = reader.readWord();
        if (objectName.isEmpty()) {
            throw new SyntaxException(MISSING_OBJECT_NAME);
        }

        List<Location> locations = Expectations.locations().read(reader);
        
        if (locations.size() == 0) {
            throw new SyntaxException(MISSING_LOCATION);
        }

        return new SpecNear(objectName, locations);
    }
}
