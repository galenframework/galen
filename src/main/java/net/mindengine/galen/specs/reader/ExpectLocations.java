package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.range;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;

public class ExpectLocations implements Expectation<List<Location>> {

    @Override
    public List<Location> read(StringCharReader reader) {
        
        List<Location> locations = new LinkedList<Location>();
        while(reader.hasMore()) {
            Range range = range().read(reader);
            List<Side> sides = Expectations.sides().read(reader);
            
            locations.add(new Location(range, sides));
        }
        return locations;
    }

}
