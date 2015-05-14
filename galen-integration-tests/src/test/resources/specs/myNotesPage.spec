@import common.spec

@objects
    caption             css     #my-notes-page  h2

    note-*              css     .list-group a
        title           css     h4
        description     css     p

    button-addnote      css     button


= Content =
    @on *
        caption:
            below menu 20 to 45 px
            inside content ~ 20px left right
            height 20 to 40px
            above note-1 10 to 20px
            aligned vertically all note-1


        note-1:
            above note-2 ~ 0px
            aligned vertically all note-2

        @for [ 1 - 2 ] as i
            note-${i}:
                height ~ 64px
                inside content ~ 20px left right

            note-${i}.title:
                inside note-${i} ~ 11 px top, ~ 16px left

            note-${i}.description:
                below note-${i}.title ~ 5 px
                inside note-${i} ~ 11 px bottom
                aligned vertically all note-${i}.title


        button-addnote:
            height ~ 45px
            below note-2 20 to 45 px

    @on desktop, tablet
        button-addnote:
            width 90 to 120px
            inside content ~ 20 px left
            aligned vertically left note-1

    @on mobile
        button-addnote:
            inside content ~ 20px left right
