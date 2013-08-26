package net.mindengine.galen.parser;

import net.mindengine.galen.suite.reader.Context;

public class BashTemplateContext extends Context {

    
    private BashTemplateContext parent;

    public BashTemplateContext(BashTemplateContext context) {
        this.parent = context;
    }

    public BashTemplateContext() {
    }

    public String process(String arguments) {
        return new BashTemplate(arguments).process(this);
    }


    @Override
    public Object getValue(String paramName) {
        if (super.containsValue(paramName)) {
            return super.getValue(paramName);
        }
        else if (parent != null) {
            return parent.getValue(paramName);
        }
        else {
            return null;
        }
    }

}
