/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
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