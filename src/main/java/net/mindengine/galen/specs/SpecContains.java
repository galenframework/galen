package net.mindengine.galen.specs;

import java.util.List;


public class SpecContains extends SpecObjectList {

    public SpecContains(List<String> list) {
        setChildObjects(list);
    }
    
}
