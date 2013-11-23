[Galen Framework](http://galenframework.com)
==============

Galen is an open-source tool for testing layout and responsive design of web applications.
For more information visit http://galenframework.com

How does it work?
------------

Galen Framework uses Selenium in order to open web browser and select the tested elements on page.
It has a special language to describe the layout of web page for different browser sizes. You just need to define your own tags for devices and sizes and then using the galen spec language write checks for each element on page. The best way to test the layout is to check location and dimensions of elements relatively to each other.
Here is a small example of basic syntax.

```
# Objects definition
=====================================
header                  id  header
menu                    css #menu
content                 id  content
side-panel              id  side-panel
footer                  id  footer
=====================================


@ all
-------------------------------
header
    inside: screen 0px top, 0px left, 0px right

menu
    inside: screen 0px left right
    below: header 0px

content
    below: menu 0px
    inside:screen 0px left

@ desktop
--------------------------------
side-panel
    below: menu 0px
    inside: screen 0px right
    width: 300px
    near: content 0px right

@ mobile
--------------------------------
content, side-panel
    width: 100% of screen/width


side-panel
    below: content 0px
```


For more information please read [Galen Framework Documentation](http://galenframework.com/docs/all/)


License
------------

Galen Framework is licensed under (Apache License, Version 2.0)[http://www.apache.org/licenses/LICENSE-2.0]
