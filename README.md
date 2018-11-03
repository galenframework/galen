[Galen Framework](http://galenframework.com)
==============
master: [![Build Status](https://travis-ci.org/galenframework/galen.svg?branch=master)](https://travis-ci.org/galenframework/galen)


Galen is an open-source tool for testing layout and responsive design of web applications. It is also a powerfull functional testing framework.
For more information visit http://galenframework.com



How does it work?
------------

Galen Framework uses Selenium in order to open web browser and select the tested elements on page.
It has a special language to describe the layout of web page for different browser sizes. You just need to define your own tags for devices and sizes and then using the galen spec language write checks for each element on page. The best way to test the layout is to check location and dimensions of elements relatively to each other.
Here is a small example of basic syntax.

```
@objects
    header                  id  header
    menu                    css #menu
    content                 id  content
    side-panel              id  side-panel
    footer                  id  footer

= Main section =
    @on *
        header:
            inside screen 0px top, 0px left, 0px right

        menu:
            inside screen 0px left right
            below header 0px

        content:
            below menu 0px
            inside screen 0px left

    @on desktop
        side-panel:
            below menu 0px
            inside screen 0px right
            width 300px
            near content 0px right

    @on mobile
        content, side-panel:
            width 100% of screen/width

        side-panel:
            below content 0px
```


And here is a more advanced spec:
```
# example of using custom rules (functions)

@set userMargin 5 to 10 px

@objects
    user-*      div.users-list .user

@rule %{pattern} are below each other by %{distance} and aligned
    @forEach [pattern] as object, prev as prevObject
        ${object}:
            below ${prevObject} ${distance}
            aligned vertically all ${prevObject}

= Checking all users =
    | user-* are below each other by ${userMargin} and aligned
```


Conditional statements:
```
@objects
    banner-container    #banner-container

= Banner section =
    @if ${isVisible("banner-container")}
        banner-container:
            image file imgs/banner.png, error 5%
```


For more information please read [Galen Framework Documentation](http://galenframework.com/docs/all/)

Contributing
------------
If you want to contribute to this project just look for current open issues. Please let know in the comments of the issue that you are going to pick it up because somebody could already work on it. In the end just send the pull request. By the way the feature that you are going to work on should not just solve your particular problem. It should be extendable and configurable. The github issues is the best place to debate on the feature and discuss how it should be implemented.

All the work on the next version is performed in corresponding release branch (e.g. release-2.5). The master branch reflects the current live version. Most of the pull requests are accepted on release branch and not on master.

If you would like to make a change to the Galen Framework website (http://galenframework.com) you can do it here https://github.com/galenframework/galenframework.com


Add [GPG key](https://www.gnupg.org/gph/en/manual.html#AEN26) to your maven settings.xml:

```
      <properties>
          <gpg.keyname>C78F3CC4</gpg.keyname>
          <gpg.passphrase>Password</gpg.passphrase>
          ...
```

Setup the dependencies
```mvn clean install```

The test can be run via
```mvn verify```

To run integrations tests against chrome:

```mvn clean verify -Dwebdriver.chrome.driver=/opt/dev/chromedriver -Dselenium.browser=chrome```

Please ensure that you downloaded chromedriver, see [here](https://sites.google.com/a/chromium.org/chromedriver/downloads)

Also you need [Maven 3.3](http://maven.apache.org/download.cgi) or greater and [Node](https://nodejs.org/download/) with some modules:
* ```sudo npm install -g grunt-cli```
* ```sudo npm install -g bower```


Building 
-----------
This project is based on Maven. You can use both Intellij or Eclipse for it. It is being developed in Linux so all the assembling scripts are in bash. In order to assemble the dist with zip archives just run ```./makeDist.sh``` and it will create a dist folder with both binary and source folders and it will prepare zip archives there as well. If you want to quickly test the concept and install galen right after the assembling you can use script ```./assembleAndInstall.sh```. It uses ```makeDist.sh``` and then just invokes ```sudo ./install.sh```


Testing
-----------
There are two levels of testing. The first one is just the regular ```mvn clean test```. But as there is a lot of Javascript code - you need [Mocha](http://mochajs.org). The tests are located in folder ```src/test/js/```. To run the just go to that folder and execute ```testJs.sh```


License
------------

Galen Framework is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
