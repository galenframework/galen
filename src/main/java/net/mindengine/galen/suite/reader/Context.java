package net.mindengine.galen.suite.reader;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    private Map<String, String> parameters = new HashMap<String, String>(); 

    public Context withParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public String getValue(String paramName) {
        return parameters.get(paramName);
    }
    
    public void putValue(String name, String value) {
        parameters.put(name, value);
    }

}
