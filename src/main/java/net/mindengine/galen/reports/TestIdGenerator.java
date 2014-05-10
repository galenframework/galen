package net.mindengine.galen.reports;

import net.mindengine.galen.utils.GalenUtils;

public class TestIdGenerator {
    private int _uniqueId = 0;
    public synchronized String generateTestId(String name) {
        _uniqueId++;
        return String.format("%d-%s", _uniqueId, GalenUtils.convertToFileName(name));
    }
    
}
