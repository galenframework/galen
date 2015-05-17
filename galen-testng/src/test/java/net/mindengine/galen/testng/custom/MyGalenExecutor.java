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
package net.mindengine.galen.testng.custom;

import java.net.MalformedURLException;

import net.mindengine.galen.runner.GalenJavaExecutor;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class MyGalenExecutor extends GalenJavaExecutor {

	@Override
	public WebDriver createDriver() throws MalformedURLException {
		// customer driver
		return new FirefoxDriver();
	}

	@Override
	public synchronized void quitDriver() {
		try {
			getDriverInstance().close();
		} catch (MalformedURLException ignored) {
			// ignore errors
		}
	}
}
