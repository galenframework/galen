package net.mindengine.galen.components;

import java.util.LinkedList;
import java.util.List;

public class JsTestRegistry {

    private static final JsTestRegistry _instance = new JsTestRegistry();
    
    public static JsTestRegistry get() {
        return _instance;
    }

    private List<String> events = new LinkedList<String>();
    
    public void registerEvent(String name) {
        this.events.add(name);
    }
    
    public List<String> getEvents() {
        return this.events;
    }
    
    public void clear() {
        events.clear();
    }
    
}
