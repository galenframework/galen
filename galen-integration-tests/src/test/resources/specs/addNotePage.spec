@import common.spec

@objects
    caption                 css     #content h2
    title-textfield         css     input[name='note.title']
    description-textfield   css     textarea
    button-add              css     button.btn-primary
    button-cancel           css     button.btn-default


= Add note page =
    @on *
        caption:
            below menu 20 to 45 px
            inside content ~ 20px left right
            height 20 to 40px
            above title-textfield 10 to 20px
            aligned vertically all title-textfield

        title-textfield:
            inside content ~ 20px left right
            below caption 10 to 20px
            inside content ~ 20px left right
            height 25 to 40px
            above description-textfield 10 to 20px

        description-textfield:
            aligned vertically all title-textfield
            height 150 to 350px


        button-add, button-cancel:
            height 40 to 50px


    @on desktop, tablet
        button-add:
            width 80 to 140px
            aligned horizontally all button-cancel
            below description-textfield 10 to 20px
            aligned vertically left description-textfield

        button-cancel:
            near button-add 0 to 10px right
            width 80 to 140px



    @on mobile
        button-add, button-cancel:
            aligned vertically all description-textfield

        button-cancel:
            below button-add 10 to 20px