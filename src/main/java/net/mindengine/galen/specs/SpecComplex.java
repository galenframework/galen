package net.mindengine.galen.specs;

import java.util.List;

public abstract class SpecComplex extends Spec {

    private String object;
    private Range range;
    private List<Location> locations;
    
    public SpecComplex(String objectName, Range range, List<Location> locations) {
        setObject(objectName);
        setRange(range);
        setLocations(locations);
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public Range getRange() {
        return range;
    }
    public void setRange(Range range) {
        this.range = range;
    }
    public List<Location> getLocations() {
        return locations;
    }
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
