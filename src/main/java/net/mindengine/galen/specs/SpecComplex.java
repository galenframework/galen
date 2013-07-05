package net.mindengine.galen.specs;

import java.util.List;

public abstract class SpecComplex extends Spec {

    private String object;
    private List<Location> locations;
    
    public SpecComplex(String objectName, List<Location> locations) {
        setObject(objectName);
        setLocations(locations);
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public List<Location> getLocations() {
        return locations;
    }
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
