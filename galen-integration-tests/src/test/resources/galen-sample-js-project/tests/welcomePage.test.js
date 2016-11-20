load("init.js");
load("pages/WelcomePage.js");

testOnAllDevices("Welcome page", null, function (driver, device) {
    new WelcomePage(driver).waitForIt();
    customCheckLayout(driver, "specs/welcomePage.gspec", device.tags);
});


