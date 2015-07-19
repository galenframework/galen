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

import static com.galenframework.parser.Expectations.range;
import static com.galenframework.suite.reader.Line.UNKNOWN_LINE;

import java.util.LinkedList;
import java.util.List;

import com.galenframework.specs.Location;
import com.galenframework.specs.Side;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.specs.Location;
import com.galenframework.specs.Range;
import com.galenframework.specs.Side;
import com.galenframework.specs.reader.StringCharReader;

public class ExpectLocations implements Expectation<List<Location>> {

    @Override
    public List<Location> read(StringCharReader reader) {
        
        List<Location> locations = new LinkedList<Location>();
        while(reader.hasMore()) {
            Range range = range().read(reader);
            List<Side> sides = Expectations.sides().read(reader);
            
            locations.add(new Location(range, sides));
            if (reader.currentSymbol() == ',') {
                reader.next();
            }
        }
        return locations;
    }

}
