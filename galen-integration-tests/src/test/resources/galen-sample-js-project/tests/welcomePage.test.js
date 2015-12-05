load("init.js");
load("pages/WelcomePage.js");

testOnAllDevices("Welcome page", "/", function (driver, device) {
    new WelcomePage(driver).waitForIt();
    customCheckLayout(driver, "specs/welcomePage.gspec", device.tags);
});

testOnAllDevices("Welcome page long words test", "/", function (driver, device) {
    new WelcomePage(driver).waitForIt();
    checkLongWordsLayout(driver, "specs/welcomePage.gspec", device.tags);
});

