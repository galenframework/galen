package net.mindengine.galen.specs;

import java.util.List;

public class SpecVertically extends SpecObjectsOnOneLine {

    public SpecVertically(Alignment alignment, List<String> list) {
        setAlignment(alignment);
        setChildObjects(list);
    }

}
