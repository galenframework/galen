
@objects
    header                   css   #header .middle-wrapper
    header-logo              id    header-logo
    header-text              css   #header h1
    menu                     css   #menu ul
    menu-item-*              css   #menu li a
    content                  css   #content
    footer                   id    footer


= Header =
    @on *
        header:
            inside screen 0px top
            centered horizontally inside screen 1px
            height ~ 70px

        header-logo:
            inside header 5 to 15px top, 0 to 10px left
            near header-text 5 to 30px left

        header-text:
            inside header 10 to 25px top

    @on desktop
        header:
            width 900px

        header-text:
            text is "Sample Website for Galen Framework"


    @on mobile, tablet
        header-text:
            text is "Sample Website"




= Menu =
    menu:
        centered horizontally inside screen 1px
        below header ~ 0px


    @on desktop
        menu:
            width 900px


    @on desktop, tablet
        menu-item-1:
            inside menu ~ 0px top left bottom

        menu-item-*:
            width 90 to 130px
            height ~ 64px
            inside menu ~ 0px top

        @forEach [menu-item-*] as menuItem, prev as previousMenuItem
            ${menuItem}:
                right-of ${previousMenuItem} 0 to 5px
                aligned horizontally all ${previousMenuItem}


    @on mobile
        menu-item-* :
            width 48 to 50% of screen/width

        @for [ 1, 2 ] as index
            menu-item-${index}:
                above menu-item-${index + 2} 0 to 5px

        @for [ 1, 3 ] as index
            menu-item-${index}:
                near menu-item-${index + 1}  0 to 5 px left


= Content =
    @on *
        content:
            below menu ~ 0px
            centered horizontally inside screen 1px


    @on desktop
        content:
            width 900px


= Footer =
    footer:
        height ~ 200px
        below content 0px
