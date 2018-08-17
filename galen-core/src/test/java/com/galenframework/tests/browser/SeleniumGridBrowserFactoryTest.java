/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.browser;

import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import com.galenframework.browser.SeleniumGridBrowserFactory;

public class SeleniumGridBrowserFactoryTest {

    // See https://github.com/galenframework/galen/issues/494
    @Test
    public void shouldParseStringAndBooleanGridArgsCorrectly() {
        // given
        Map<String, String> inputCaps = new HashMap<>();
        inputCaps.put("marionette", "true");
        inputCaps.put("anString", "42");
        // when
        SeleniumGridBrowserFactory factory = new SeleniumGridBrowserFactory("http://mygrid.com");
        factory.setBrowser("firefox");
        factory.setDesiredCapabilites(inputCaps);
        DesiredCapabilities usedCapsForWebDriver = factory.createCapabilities();
        // then
        assertTrue((Boolean) usedCapsForWebDriver.getCapability("marionette"));
        assertTrue(usedCapsForWebDriver.getBrowserName().equalsIgnoreCase("firefox"));
    }
}
