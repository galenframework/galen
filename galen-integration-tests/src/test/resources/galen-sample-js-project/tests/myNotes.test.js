load("init.js");
load("pages/LoginPage.js");
load("pages/MyNotesPage.js");
load("commons.js");

testOnAllDevices("My notes page", null, function (driver, device) {
    loginAsTestUser(driver);
    customCheckLayout(driver, "specs/myNotesPage.gspec", device.tags);
});
