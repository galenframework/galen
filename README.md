[Galen Framework](http://galenframework.com)
==============

[![Join the chat at https://gitter.im/galenframework/galen](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/galenframework/galen?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/galenframework/galen?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Galen is an open-source tool for testing layout and responsive design of web applications. It is also a powerfull functional testing framework.
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


@ *
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

Contributing
------------
If you want to contribute to this project just look for current open issues. Please let know in the comments of the issue that you are going to pick it up because somebody could already work on it. In the end just send the pull request. By the way the feature that you are going to work on should not just solve your particular problem. It should be extendable and configurable. The github issues is the best place to debate on the feature and discuss how it should be implemented.

If you would like to make a change to the Galen Framework website (http://galenframework.com) you can do it here https://github.com/galenframework/galenframework.com

If you want to improve the functionality related to image comparison or color scheme - you have to first make a pull request to [rainbow4j](https://github.com/galenframework/rainbow4j) as all the core code for comparing images, applying filters and calculating the color scheme is located in it. After this it will be released to sonatype central repository and will be available as maven dependency.

Add GPG key to your maven settings.xml:

```
      <properties>
          <gpg.keyname>C78F3CC4</gpg.keyname>
          <gpg.passphrase>Password</gpg.passphrase>
          ...
```

The test can be run via
```mvn verify```

To run integrations tests against chrome:

```mvn clean verify -Dwebdriver.chrome.driver=/opt/dev/chromedriver -Dselenium.browser=chrome```

Please ensure that you download chromedriver, see [here](https://sites.google.com/a/chromium.org/chromedriver/downloads)



Building 
-----------
This project is based on Maven. You can use both Intellij or Eclipse for it. It is being developed in Linux so all the assembling scripts are in bash. In order to assemble the dist with zip archives just run ```./makeDist.sh``` and it will create a dist folder with both binary and source folders and it will prepare zip archives there as well. If you want to quickly test the concept and install galen right after the assembling you can use script ```./assembleAndInstall.sh```. It uses ```makeDist.sh``` and then just invokes ```sudo ./install.sh```


Testing
-----------
There are two levels of testing. The first one is just the regular ```mvn clean test```. But as there is a lot of Javascript code - you need [Mocha](http://mochajs.org). The tests are located in folder ```src/test/js/```. To run the just go to that folder and execute ```testJs.sh```


License
------------

Galen Framework is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)