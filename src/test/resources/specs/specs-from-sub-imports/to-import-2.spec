
=====================

menu        css #item0
menu-item-1 css #item1
menu-item-2 css #item2
menu-item-3 css #item3
=====================


menu
    width: 100px
    text is: there are ${findAll("menu-item-*").length} items

[ 1 - ${count("menu-item-*")} ]
menu-item-@
    height: 50px