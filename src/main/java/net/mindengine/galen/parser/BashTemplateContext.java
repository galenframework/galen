package net.mindengine.galen.parser;

import net.mindengine.galen.suite.reader.Context;

public class BashTemplateContext extends Context {

    public String process(String arguments) {
        return new BashTemplate(arguments).process(this);
    }


}
