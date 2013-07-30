

=====================================================================
header                      id          header
header-with-corrections     corrections (0, 0, -100, -30)       id  header
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



main                        id          main

=====================================================================



@ all
--------------------------------
header
    contains:   header-text-1, header-text-2
    near:   menu 0px top
    width: 1000px
    height: 100 to 120px

header-with-corrections:
    width: 900px
    height: 70 to 100px

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
    horizontally centered: menu-item-categories, menu-item-blog, menu-item-rss, menu-item-about, menu-item-contacts, menu-item-help
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
    width: 200 to 300px
    

@ mobile
------------------------------
header
    height: 50 to 70px


