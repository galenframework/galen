
=====================

menu        css #item0
menu-item-3 css #item3
menu-item-1 css #item1
menu-item-2 css #item2
=====================


menu
    width: 100px
    text is: items are ${var all = findAll("menu-item-*"); all.length + ":" + all[0].name + ":" + all[1].name + ":" + all[2].name}

[ 1 - ${count("menu-item-*")} ]
menu-item-@
    height: 50px