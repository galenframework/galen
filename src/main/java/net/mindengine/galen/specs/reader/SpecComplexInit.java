package net.mindengine.galen.specs.reader;

import net.mindengine.galen.specs.Spec;

public interface SpecComplexInit {
    Spec init(String specName, Object[] args);
}
