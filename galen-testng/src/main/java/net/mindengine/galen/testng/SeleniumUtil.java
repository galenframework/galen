package net.mindengine.galen.testng;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SeleniumUtil {

    public static DesiredCapabilities getBrowserCapabilities(String driverParameter) {
        DesiredCapabilities capabilities = null;
        if (driverParameter.equalsIgnoreCase(BrowserType.FIREFOX)) {
            capabilities = DesiredCapabilities.firefox();
        }
        if (driverParameter.equalsIgnoreCase(BrowserType.IEXPLORE)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        }

        if (driverParameter.equalsIgnoreCase(BrowserType.CHROME) || driverParameter == null) {
            // chrome runs much faster
            capabilities = DesiredCapabilities.chrome();
        }
        return capabilities;
    }
}
