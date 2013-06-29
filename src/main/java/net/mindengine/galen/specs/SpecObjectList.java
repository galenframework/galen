package net.mindengine.galen.specs;

import java.util.List;

public abstract class SpecObjectList extends Spec {
    
    private List<String> childObjects;

    public List<String> getChildObjects() {
        return childObjects;
    }

    public void setChildObjects(List<String> childObjects) {
        this.childObjects = childObjects;
    }
}
