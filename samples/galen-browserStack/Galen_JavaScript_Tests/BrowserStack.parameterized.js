// A Galen-JavaScript test to execute a spec against multiple configurations

// Configurations for running the tests
this.devices = {
  // For mobile devices
  mobile: {
    tag: "mobile",
    deviceName: "iPhone 5S",
    browserName: "iPhone",
    platform: "MAC",
    device: "iPhone 5S",
    browser: "",
    browser_version: "",
    os: "",
    os_version: "",
    emulator: "true"
  },
  // For desktop browsers
  desktop: {
    tag: "desktop",
    deviceName: "Win-Chrome 43",
    browserName: "",
    platform: "",
    device: "",
    browser: "Chrome",
    browser_version: "43",
    os: "Windows",
    os_version: "8.1",
    emulator: ""
  }
};

forAll(devices, function (option) {
	test("Homepage Test on ${deviceName}", function() {
		var driver = createGridDriver("http://" +  System.getProperty("browserstack.username") + ":" +  System.getProperty("browserstack.key") + "@hub.browserstack.com/wd/hub", {
			desiredCapabilities: {
				 browser: option.browser,
				 browser_version: option.browser_version,
				 os: option.os,
				 os_version: option.os_version,
				 browserName: option.browserName,
				 platform: option.platform,
				 device: option.device,
         emulator: option.emulator,
				 "browserstack.debug": "true"
			}
		});

		// Open the test URL
		driver.get("http://www.google.com/ncr");

		// Select the Spec
		checkLayout(driver, "homepage.gspec", [option.tag]);

		// Destroy the session
		driver.quit();
	});
});