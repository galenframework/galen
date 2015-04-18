@@ import common.spec

========================================
caption             css     #my-notes-page  h2

note-*              css     .list-group a
note-*-title        css     .list-group a h4
note-*-description  css     .list-group a p

button-addnote      css     button
========================================



@ Content | *
-----------------------------

caption
    below: menu 20 to 45 px
    inside: content ~ 20px left right
    height: 20 to 40px
    above: note-1 10 to 20px
    aligned vertically all: note-1


[ 1 - 2 ]
note-@
    height: ~ 64px
    inside: content ~ 20px left right

[ 1 - 2 ]
note-@-title
    inside: note-@ ~ 11 px top, ~ 16px left

[ 1 - 2 ]
note-@-description
    below: note-@-title ~ 5 px
    inside: note-@ ~ 11 px bottom
    aligned vertically all: note-@-title

note-1
    above: note-2 ~ 0px
    aligned vertically all: note-2


button-addnote
    height: ~ 45px
    below: note-2 20 to 45 px

@ ^ | desktop, tablet
------------------------
button-addnote
    width: 90 to 120px
    inside: content ~ 20 px left
    aligned vertically left: note-1

@ ^ | mobile
------------------
button-addnote
    inside: content ~ 20px left right
