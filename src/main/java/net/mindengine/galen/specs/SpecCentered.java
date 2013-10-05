package net.mindengine.galen.specs;

public class SpecCentered extends Spec {

	public enum Alignment {
		ALL, VERTICALLY, HORIZONTALLY;
		
		public static Alignment fromString(String alignmentText) {
		    if ("vertically".equals(alignmentText)) {
                return VERTICALLY;
            }
		    else if ("horizontally".equals(alignmentText)) {
                return HORIZONTALLY;
            }
		    else if ("all".equals(alignmentText)) {
                return ALL;
            }
		    else throw new IllegalArgumentException("Can't read alignment: " + alignmentText);
		}
	}
	
	public enum Location {
		ON, INSIDE;

		public static Location fromString(String locationText) {
			if ("on".equals(locationText)) {
				return ON;
			}
			else if ("inside".equals(locationText)) {
				return INSIDE;
			}
			else throw new IllegalArgumentException("Can't read location: " + locationText);
		}
	}
	
	
	private String object;
	private Alignment alignment;
	private Location location;
	
	public SpecCentered() {
		
	}
	public SpecCentered(String object, Alignment alignment, Location location) {
		this.object = object;
		this.alignment = alignment;
		this.location = location;
	}
	
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public Alignment getAlignment() {
		return alignment;
	}
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
