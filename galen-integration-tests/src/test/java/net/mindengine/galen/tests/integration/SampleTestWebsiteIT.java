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
