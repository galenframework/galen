package net.mindengine.galen.reports;

public class  UniqueIdGenerator {
    private long _uniqueId;

    public synchronized long uniqueId() {
        _uniqueId++;
        return _uniqueId;
    }
}
