

@objects
    header                  id  header
    header-text-1           css #header h1
    header-text-2           css #header h2
    menu                    id  menu

= Main section =
    @on mobile
        header :
            height 150 to 185px

        header-text-1 :
            inside header 20 px top left right

    @on desktop
        header :
            height 100px

    @on mobile, debug
        menu :
            below header 20px
            aligned vertically left header


    @on mobile
        menu :
            width 100% of header/width



