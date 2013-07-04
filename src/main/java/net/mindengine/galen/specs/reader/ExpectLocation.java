package net.mindengine.galen.specs.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Location;

public class ExpectLocation implements Expectation<List<Location>>{

    @Override
    public List<Location> read(StringCharReader reader) {
        ExpectWord expectWord = new ExpectWord();
        
        List<Location> locations = new LinkedList<Location>();
        
        while(reader.hasMore()) {
            String location = expectWord.read(reader);
            if (!location.isEmpty()) {
                locations.add(Location.fromString(location));
            }
        }
        return locations;
    }
    
    
}
