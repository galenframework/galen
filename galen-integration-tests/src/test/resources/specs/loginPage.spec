@import common.spec

@objects
    login-box           id  login-page
    login-caption       css #login-page h2

    username-textfield  css input[name='login.username']
    password-textfield  css input[name='login.password']

    login-button        css .button-login
    cancel-button       css .button-cancel


= Login box =
    @on *
        login-box:
            centered horizontally inside content 1px
            below menu 20 to 45px

        login-caption:
            height 20 to 35px
            text is "Login"

        username-textfield, password-textfield:
            height 25 to 35 px

        username-textfield:
            below login-caption 5 to 15px
            aligned vertically all password-textfield

        password-textfield:
            below username-textfield 5 to 15px
            aligned vertically left login-button

        login-button, cancel-button:
            height 40 to 50 px

        login-button:
            text is "Login"

        cancel-button:
            text is "Cancel"


    @on desktop, tablet
        login-box:
            width 400px

        login-caption:
            inside login-box ~ 40px top, ~ 20px left

        username-textfield, password-textfield:
            inside login-box ~ 20px left right

        login-button:
            below password-textfield 5 to 15px
            width 70 to 90 px
            aligned horizontally all cancel-button

        cancel-button:
            width 80 to 100 px
            near login-button 3 to 8px right


    @on mobile
        login-box:
            inside screen ~ 20px left right

        username-textfield, password-textfield:
            inside login-box 0px left right

        login-caption:
            inside login-box 0px top, 0px left

        login-button:
            inside login-box 0px left right
            above cancel-button  4 to 10px
            aligned vertically all cancel-button
