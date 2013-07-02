package net.mindengine.galen.specs;

import java.util.List;

public class SpecHorizontally extends SpecObjectsOnOneLine {

    public SpecHorizontally(Alignment alignment, List<String> list) {
        setAlignment(alignment);
        setChildObjects(list);
    }
}
