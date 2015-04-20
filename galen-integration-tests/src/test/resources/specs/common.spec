
==============================================================

header                   css   #header .middle-wrapper
header-logo              id    header-logo
header-text              css   #header h1

menu                     css   #menu ul
menu-item-*              css   #menu li a

content                  css   #content

footer                   id    footer

==============================================================


@ Header | *
---------------------------------------------

header
    inside: screen 0px top
    centered horizontally inside: screen 1px
    height: ~ 70px

header-logo
    inside: header 5 to 15px top, 0 to 10px left
    near: header-text 5 to 30px left

header-text
    inside: header 10 to 25px top


@ ^ | desktop
----------------------
header
    width: 900px

header-text
    text is: Sample Website for Galen Framework


@ ^ | mobile, tablet
----------------------
header-text
    text is: Sample Website




@ Menu | *
-----------------------------------------------
menu
    centered horizontally inside: screen 1px
    below: header ~ 0px


@ ^ | desktop
------------------------
menu
    width: 900px


@ ^ | desktop, tablet
-----------------------
menu-item-1
    inside: menu ~ 0px top left bottom

[ 1 - 4 ]
menu-item-@
    width: 90 to 130px
    height: ~ 64px
    inside: menu ~ 0px top

[ 1 - 3 ]
menu-item-@
    near: menu-item-@{+1} 0 to 5px left
    aligned horizontally all: menu-item-@{+1}


@ ^ | mobile
-----------------------
[1 - 4]
menu-item-@
    width: 48 to 50% of screen/width

[ 1, 2 ]
menu-item-@
    above: menu-item-@{+2} 0 to 5px

[ 1, 3 ]
menu-item-@
    near: menu-item-@{+1}  0 to 5 px left





@ Content | *
-----------------------------------------
content
    below: menu ~ 0px
    centered horizontally inside: screen 1px


@ ^ | desktop
------------------
content
    width: 900px


@ Footer | *
-----------------------------------------
footer
    height: ~ 200px
    below: content 0px 
