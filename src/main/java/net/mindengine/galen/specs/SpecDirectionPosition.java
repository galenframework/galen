package net.mindengine.galen.specs;

public abstract class SpecDirectionPosition extends Spec {

	public SpecDirectionPosition(String object, Range range) {
		this.object = object;
		this.range = range;
	}
	private String object;
	private Range range;
	
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
}
