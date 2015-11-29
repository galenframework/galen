load("WelcomePage.js");

this.LoginPage = $page("Login page", {
    username: "input[name='login.username']",
    password: "input[name='login.password']",
    loginButton: "button.button-login"
}, {
    // Declaring secondary fields so they are not used in 'waitForIt' function
    errorMessage: "#login-error-message",

    loginAs: loggedFunction ("Log in as ${_1.username} with password ${_1.password}", function(user) {
        this.username.typeText(user.username);
        this.password.typeText(user.password);
        this.loginButton.click();
    }) 
});
