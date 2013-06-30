package net.mindengine.galen.specs.reader;

import net.mindengine.galen.specs.Spec;

public class SimpleSpecProcessor implements SpecProcessor {

    private SpecInit specInit;

    public SimpleSpecProcessor(SpecInit specInit) {
        this.specInit = specInit;
    }

    @Override
    public Spec processSpec(String paramsText) {
        if (paramsText != null && !paramsText.isEmpty()) {
            throw new IncorrectSpecException("This spec doesn't take any parameters");
        }
        return specInit.init();
    }

}
