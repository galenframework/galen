package net.mindengine.galen.api;

import java.util.List;

import org.openqa.selenium.Dimension;

import net.mindengine.galen.api.external.TestDevice;

public class GalenTestDevice implements TestDevice {

	private final String name;
	private final Dimension screenSize;
	private final List<String> tags;

	public GalenTestDevice(String name, Dimension screenSize, List<String> tags) {
		this.name = name;
		this.screenSize = screenSize;
		this.tags = tags;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Dimension getScreenSize() {
		return screenSize;
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GalenTestDevice [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (screenSize != null) {
			builder.append("screenSize=");
			builder.append(screenSize);
			builder.append(", ");
		}
		if (tags != null) {
			builder.append("tags=");
			builder.append(tags);
		}
		builder.append("]");
		return builder.toString();
	}

}
