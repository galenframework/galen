package net.mindengine.galen.suite.reader;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    private Map<String, Object> parameters = new HashMap<String, Object>(); 

    public Context withParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Object getValue(String paramName) {
        return parameters.get(paramName);
    }
    
    public void putValue(String name, Object value) {
        parameters.put(name, value);
    }

    public boolean containsValue(String paramName) {
        return parameters.containsKey(paramName);
    }

    
    public void addValuesFromMap(Map<String, String> map) {
        parameters.putAll(map);
    }
}
