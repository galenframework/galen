package net.mindengine.galen.api;

public class UnregisteredTestSession extends RuntimeException {
    public UnregisteredTestSession(String message) {
        super(message);
    }
}
