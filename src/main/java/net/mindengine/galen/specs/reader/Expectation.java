package net.mindengine.galen.specs.reader;

public interface Expectation<T> {
    public T read(StringCharReader charReader);
    
}
