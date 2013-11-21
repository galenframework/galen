package net.mindengine.galen.specs;

public class SpecObjectWithErrorRate extends Spec {

    private String object;
    private int errorRate = 2;
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public int getErrorRate() {
        return errorRate;
    }
    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }
    
}
