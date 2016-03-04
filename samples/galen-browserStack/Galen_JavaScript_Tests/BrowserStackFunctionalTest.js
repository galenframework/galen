// A Galen-JavaScript test to interact with the browser

test("Homepage Test on BrowserStack", function() {

	// Create a session on BrowserStack
	var driver = createGridDriver("http://" +  System.getProperty("browserstack.username") + ":" +  System.getProperty("browserstack.key") + "@hub.browserstack.com/wd/hub", {
		// Define capabilities
		desiredCapabilities: {
			 browser: "Chrome",
			 browser_version: "43.0",
			 os: "OS X",
			 os_version: "Mavericks",
			 "browserstack.debug": "true"
		}
	});

	// Test code
	driver.get('http://www.google.com/ncr');
	driver.findElement(By.name('q')).sendKeys('BrowserStack');
	driver.findElement(By.name('btnG')).click();
	
	// Capture screenshot
	var file = takeScreenshot(driver);
	var path = file.getAbsolutePath();

	// Destroy the session
	driver.quit();
});