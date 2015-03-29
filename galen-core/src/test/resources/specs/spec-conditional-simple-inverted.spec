

===============================
textfield    id     textfield
button-1     id     button-1
button-2     id     button-2
===============================





@ mobile
------------------------
textfield
    height: 100px

@@ if not
button-1
    width: > 100px
    
button-2 
    height: < 50px
    width: 200px
    
@@ do
button-1
    above: button-2 0px 

button-2
    below: button-1 0px
@@ end


textfield
    width: 100px