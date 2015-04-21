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
package net.mindengine.galen.tests.integration;

import net.mindengine.galen.util.GalenBaseTestRunner;

import org.testng.annotations.Test;

public class SampleTestWebsiteIT extends GalenBaseTestRunner {

    @Test(dataProvider = "devices")
    public void welcomePage_shouldLookGood_onDevice(final TestDevice device) throws Exception {
        verifyPage("sample-test-website/index.html", device, "/specs/welcomePage.spec");
    }

    @Test(dataProvider = "devices")
    public void loginPage_shouldLookGood_onDevice(final TestDevice device) throws Exception {
        verifyPage("sample-test-website/login.html", device, "/specs/loginPage.spec");
    }

}
