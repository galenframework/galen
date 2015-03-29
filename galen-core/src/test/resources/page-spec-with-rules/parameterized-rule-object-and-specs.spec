

=========================
login-button    .login
=========================


@@ rule: %{element} should have %{pixels} pixels height
    ${element}
        height: ${pixels} px
@@ end



| login-button should have 100 pixels height