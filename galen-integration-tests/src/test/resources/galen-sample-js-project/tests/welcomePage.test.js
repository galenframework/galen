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


testOnDevice(devices.desktopFirefox, "Menu Highlight", "/", function (driver, device) {
    var welcomePage = new WelcomePage(driver).waitForIt();
    logged("Checking color for menu item", function () {
        customCheckLayout(driver, "specs/menuHighlight.gspec", ["usual"]);
    })

    logged("Checking color for highlighted menu item", function () {
        welcomePage.hoverFirstMenuItem();
        customCheckLayout(driver, "specs/menuHighlight.gspec", ["hovered"]);
    });
});
