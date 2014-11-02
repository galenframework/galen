=====================
menu-item-*    css #menu li
=====================

@@ set spaceX ${find("menu-item-2").left() - find("menu-item-1").right()}
@@ set spaceY ${find("menu-item-2").top() - find("menu-item-1").bottom()}

menu-item-3
    near: menu-item-4 ${spaceX}px left
    above: menu-item-4 ${spaceY}px
