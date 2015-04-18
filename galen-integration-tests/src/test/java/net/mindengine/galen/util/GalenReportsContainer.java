package net.mindengine.galen.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.mindengine.galen.reports.GalenTestInfo;

public class GalenReportsContainer {

	private static final GalenReportsContainer _instance = new GalenReportsContainer();

	private final ThreadLocal<Collection<GalenTestInfo>> tests = new ThreadLocal<Collection<GalenTestInfo>>();

	private GalenReportsContainer() {
		tests.set(new HashSet<GalenTestInfo>());
	}

	public static final GalenReportsContainer get() {
		return _instance;
	}

	public GalenTestInfo registerTest(final String method) {
		GalenTestInfo testInfo = GalenTestInfo.fromString(method);
		tests.get().add(testInfo);
		return testInfo;
	}

	public List<GalenTestInfo> getAllTests() {
		return new ArrayList<GalenTestInfo>(tests.get());
	}

}