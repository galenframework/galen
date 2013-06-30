package net.mindengine.galen.specs.reader;

import net.mindengine.galen.specs.Spec;

public interface SpecProcessor {

    public Spec processSpec(String paramsText);
}
