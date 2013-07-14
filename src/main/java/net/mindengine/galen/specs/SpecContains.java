package net.mindengine.galen.specs;

import java.util.List;


public class SpecContains extends SpecObjectList {

    private boolean isPartly = false;
    
    public SpecContains(List<String> list, boolean isPartly) {
        setChildObjects(list);
        setPartly(isPartly);
    }
    public boolean isPartly() {
        return isPartly;
    }
    public void setPartly(boolean isPartly) {
        this.isPartly = isPartly;
    }
    
}
