
@objects
    button-save         css .save-button
    name-textfield      id  name-textfield
    menu-item-*         css .menu-item

    big-container       css .container

    unexistent-element  css .i-do-not-exist

    invisible-element   id i-am-not-visible


# there was a bug when page dump couldn't parse the spec containing variables

@set var1 23324


= Main section =
    button-save :
        width 100px
        height ${var1} px