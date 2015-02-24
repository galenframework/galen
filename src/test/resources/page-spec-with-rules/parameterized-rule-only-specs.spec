======================
login-button .login
======================


@@ rule: should be a square with %{size} pixels size
    width: ${size} px
    height: ${size} px
@@ end


login-button
    | should be a square with 50 pixels size