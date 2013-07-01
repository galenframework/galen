package net.mindengine.galen.specs;

public abstract class SpecRange extends Spec {

    private Range range;

    public SpecRange(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
}
