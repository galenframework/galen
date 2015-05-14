

@objects
    password-textfield   css input[name='login.password']
    login-button         css .button-login
    error-message        id   login-error-message


= Error message =
    @on *
        error-message:
            below password-textfield 5 to 15px
            height ~ 52px
            aligned vertically all password-textfield
            above login-button 10 to 30px


    @on mobile
        error-message:
            aligned vertically all login-button
