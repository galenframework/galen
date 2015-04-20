@@ import common.spec

==========================================================
welcome-block       css     .jumbotron
greeting            css     #welcome-page h1
text-block-*        css     #welcome-page p
login-button        css     #welcome-page .button-login
==========================================================



@ Content | *
----------------------------------------------------------
welcome-block
    inside: content ~ 20px top left right

text-block-1, login-button, text-block-3
    inside: welcome-block ~30px left

greeting
    above: text-block-1 10 to 50 px
    inside: welcome-block ~ 30px left

text-block-1
    height: > 20px
    above: login-button 10 to 50 px

login-button
    height: ~ 45px
    text is: Login
    above: text-block-3 10 to 50px




@ ^ | desktop
-----------------------
greeting
    height: ~ 69px
    inside: welcome-block ~ 68 px top

login-button
    width: ~ 78px


@ ^ | tablet
------------------------
greeting
    height: ~ 39px
    inside: welcome-block ~ 50 px top

login-button
    width: ~ 78px

@ ^ | mobile
------------------------
greeting
    height: ~ 78px
    inside: welcome-block ~ 50 px top

login-button
    inside: welcome-block ~ 30px left right
