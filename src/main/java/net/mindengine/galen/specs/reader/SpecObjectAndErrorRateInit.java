package net.mindengine.galen.specs.reader;

import net.mindengine.galen.specs.Spec;

public interface SpecObjectAndErrorRateInit {
    public Spec init(String specName, String objectName, Integer errorRate);
}
