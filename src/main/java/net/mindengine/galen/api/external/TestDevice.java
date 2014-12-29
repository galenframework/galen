package net.mindengine.galen.api.external;

import java.util.List;

import org.openqa.selenium.Dimension;

public interface TestDevice {

	public String getName();

	public Dimension getScreenSize();

	public List<String> getTags();
}
