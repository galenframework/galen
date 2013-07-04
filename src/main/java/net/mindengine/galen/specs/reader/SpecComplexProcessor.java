package net.mindengine.galen.specs.reader;

import java.util.List;

import net.mindengine.galen.specs.Spec;

public class SpecComplexProcessor implements SpecProcessor {

    private List<Expectation<?>> toExpect;
    private SpecComplexInit specInit;

    public SpecComplexProcessor(List<Expectation<?>> toExpect, SpecComplexInit specInit) {
        this.toExpect = toExpect;
        this.specInit = specInit;
    }

    @Override
    public Spec processSpec(String specName, String paramsText) {
        StringCharReader reader = new StringCharReader(paramsText);
        
        Object[]args = new Object[toExpect.size()];
        int i=0;
        for(Expectation<?> expectation : toExpect) {
            args[i] = expectation.read(reader);
            i++;
        }
        return specInit.init(specName, args);
    }

}
