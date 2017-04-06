# Galen-BrowserStack
Perform Automated Layout Testing using Galen Framework on BrowserStack.

## BrowserStack
BrowserStack is a cross-browser testing tool, to test public websites and protected servers, on a cloud infrastructure of desktop and mobile browsers. For more information visit https://www.browserstack.com.

## Galen Framework
Galen is an open-source tool for testing layout and responsive design of web applications. It is also a powerfull functional testing framework. For more information visit http://galenframework.com.

## How to Install Galen
* For installing Galen on OSX and Linux visit http://galenframework.com/docs/getting-started-install-galen
* For configuring Galen on Windows visit http://mindengine.net/post/2014-01-08-configuring-galen-framework-for-windows

## Run Galen Tests on BrowserStack
Just a few things you should ensure before running Galen tests on BrowserStack:
* First you need to have an account at BrowserStack ([Sign-up](https://www.browserstack.com/users/sign_in)). The free trial gets you access to 100 minutes of BrowserStack Automate with 5 parallel runs which should be enough for you to try out your Galen tests. 
* Get your Automate `Username` and `Access Key` from [here](https://www.browserstack.com/accounts/automate), after you login to your account. 
* Add these credentials to the test files, to point your tests to BrowserStack's Selenium Hub.

Here are the two kinds of galen tests which you can run on BrowserStack:
#### Galen Specs
Using Galen Specs Language you are able to describe any complex layout including different screen sizes or browsers. It's not only easy to write, it is also easy to read if you are unfamiliar with the language. A list of all capabilities for running tests on various BrowserStack platforms can be found [here](https://www.browserstack.com/automate/capabilities).

Command to execute the Galen Spec test:
```
galen test BrowserStackTest.test --parallel-suites 2 -Dbrowserstack.username=<USERNANME> -Dbrowserstack.key=<KEY>
```

#### Galen JavaScript Tests
With JavaScript tests you are free to invent your own test framework and perform a lot of complex stuff. You can execute tests against a Galen Spec on a single congiguration or parameterize your test to run it against multiple configurations. You also have the flexibility to write functional tests using which you can interact with the browser elements.

Command to execute the Galen JavaScript test:
```
galen test BrowserStack.test.js --parallel-suites 2 -Dbrowserstack.username=<USERNANME> -Dbrowserstack.key=<KEY>
```

#### Command line arguments (More information [here](http://galenframework.com/docs/reference-working-in-command-line/#Runningtestsuites)):
* _htmlreport_ - path to folder in which Galen should generate html reports
* _testngreport_ - path to xml file in which Galen should write testng report
* _parallel-suites_ - amount of threads for running tests in parallel
* _recursive_ - flag which is used in case you want to search for all .test files recursively in folder
* _filter_ - a filter for a test name

## Additional Links
* Selenium Testing on BrowserStack - https://www.browserstack.com/automate
* Galen Specs Language Guide - http://galenframework.com/docs/reference-galen-spec-language-guide
* Galen JavaScript Test Guide - http://galenframework.com/docs/reference-javascript-tests-guide
