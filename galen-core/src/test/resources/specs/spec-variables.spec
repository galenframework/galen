
====================
header    id header
container id container
====================



@@ Set margin 10 to 20px

@@ Set    margin2             ~ 10px
@@ Set    header.suffix           Hi
@@ Set    header.text         ${header.suffix}, welcome   
    
    
header
    text is: ${header.text}
    inside: screen ${margin} top
    
container
    below: header ${margin2}
