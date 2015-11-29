load("pages/LoginPage.js");
load("pages/MyNotesPage.js");

function loginAsTestUser(driver) {
    new WelcomePage(driver)
        .waitForIt()
        .loginButton.click();

    new LoginPage(driver)
        .waitForIt()
        .loginAs(TEST_USER);

    return new MyNotesPage(driver).waitForIt();
}