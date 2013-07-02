package net.mindengine.galen.specs.reader;

import java.util.List;

import net.mindengine.galen.specs.Spec;

public interface SpecListInit {

    Spec init(String specName, List<String> list);

}
