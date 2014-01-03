/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.parser;

import static net.mindengine.galen.parser.Expectations.range;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.reader.StringCharReader;

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
        if (locations.size() == 0) {
            throw new SyntaxException(UNKNOWN_LINE, "There is no location defined");
        }
        return locations;
    }

}
