

=====================================================================
header                      id          header
header-with-corrections     @ (0, 0, -100, -30)       id  header
header-text-1               css         #header h1
header-text-2               css         #header h2


menu                        id          menu
menu-item-home              xpath       //div[@id="menu"]//li/a[.='Home']
menu-item-categories        xpath       //div[@id="menu"]//li/a[.='Categories']
menu-item-blog              xpath       //div[@id="menu"]//li/a[.='Blog']
menu-item-rss               xpath       //div[@id="menu"]//li/a[.='Rss']
menu-item-about             xpath       //div[@id="menu"]//li/a[.='About']
menu-item-contacts          xpath       //div[@id="menu"]//li/a[.='Contacts']
menu-item-help              xpath       //div[@id="menu"]//li/a[.='Help']


menu-items-*                css         #menu  li a

main                        id          main

feedback                    id          feedback
=====================================================================



@ all
--------------------------------
header
    contains:   header-text-1, header-text-2
    near:   menu 0px top
    width: 1000px
    height: 100 to 140px

header-with-corrections:
    width: 900px
    height: 70 to 110px

header-text-1
    near:   header-text-2       0px top
    inside: header              20 to 30px top left
    
header-text-2
    near:   header-text-1       0px bottom
    inside: header              20 to 30px left 

menu:
    near:   header              0px bottom
    near:   main                0px top

menu-item-home:
    aligned horizontally centered: menu-item-categories, menu-item-blog, menu-item-rss, menu-item-about, menu-item-contacts, menu-item-help
    near: menu-item-categories 0px left
    inside: menu 0 to 1px top left bottom

menu-item-categories:
    inside: menu 0 to 1px top bottom
    near: menu-item-blog 0px left


@ desktop
------------------------------
header
    width: 1000px
    height: 30 to 40px


@ tablet, mobile
-----------------------------
header
    height: 150 to 185px    




@ mobile
------------------------------
menu-item-home:
    aligned horizontally: menu-item-categories, menu-item-blog

menu-item-rss:
    aligned horizontally: menu-item-about, menu-item-contacts
    near: menu-item-home 0px bottom
    
    


@ screen-object-check
-----------------------------
header
    width: ~100% of screen/width
    
    
@ viewport-object-check
-----------------------------
feedback
    inside:viewport 0 to 20px right bottom
    



@ multiple-objects-check
-----------------------------
menu-item-*:
    height: 40 to 50px

header-text-1, header-text-2
    width: 900 to 1000px




@ text-check 
----------------------------
menu-item-home:
    text is: Home
    
menu-item-rss:
    text matches: R.*s
    
menu-item-categories:
    text starts: Cat
    
menu-item-categories:
    text ends: ries
    
menu-item-categories:
    text contains: egor


@ multi-check
----------------------------
menu-items-*
    height: 40 to 50px
    
menu-items-1
    near: menu-items-2 0px left
    
    
    
