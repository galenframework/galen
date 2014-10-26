var assert = require("assert"),
    should=require("should"),
    GalenPages = require("./../../main/resources/js/GalenPages").GalenPages,
    assertThat = require("./assertThat.js").assertThat,
    assertError = require("./assertThat.js").assertError;

var dummyDriver = {};

// By object is redefined in order to test that locators are properly converted
By = {
    id: function (value) {
        return {t: "id", v: value};
    },
    cssSelector: function (value) {
        return {t: "css", v: value};
    },
    xpath: function (value) {
        return {t: "xpath", v: value};
    }
};
function toJson(obj) {
    return JSON.stringify(obj);
};

// Thread object is mocked in order test how galen wait for pages
Thread = {
    sleep: function (time) {
    }
}

/*
 * Mocking a WebDriver
 */
function RecordingDriver() {
    this.actions = [];
    this.get = function (url) {
        this.record("#get " + url);
    };
    this.record = function (action) {
        this.actions[this.actions.length] = action;
    };
    this.clearActions = function () {
        this.actions = [];
    };
    this.findElement = function (by) {
        this.record("#findElement " + toJson(by));
        return new RecordingWebElement(by);
    };
    this.navigate = function () {
        return new RecordingDriverNavigation(this);
    },
    this.getCurrentUrl = function () {
        this.record("#getCurrentUrl");
        return "http://fakeurl.fake";
    },
    this.getPageSource = function () {
        this.record("#getPageSource");
        return "<fake>page source</fake>";
    },
    this.getTitle = function () {
        this.record("#getTitle");
        return "Fake title";
    }
}

/*
 * Mocking a WebDriver.Navigatation
 */
function RecordingDriverNavigation(driver) {
    this.driver = driver;
    this.reload = function () {
        this.driver.record("#navigate().reload");
    },
    this.back = function () {
        this.driver.record("#navigate().back");
    }
}

/*
 * Mocking a WebElement
 */
function RecordingWebElement(locator) {
    this.actions = [];
    this.locator = locator;
    this.click = function () {
        this.record("#click");
    };
    this.sendKeys = function (keys) {
        this.record("#sendKeys " + keys);
    };
    this.clear = function () {
        this.record("#clear");
    };
    this.isDisplayed = function () {
        this.record("#isDisplayed");
        return true;
    };
    this.isEnabled = function () {
        this.record("#isEnabled");
        return true;
    };
    this.getAttribute = function (attrName) {
        this.record("#getAttribute " + attrName);
        return "";
    };
    this.getCssValue = function (cssProperty) {
        this.record("#getCssValue " + cssProperty);
        return "";
    };
    this.record = function (action) {
        this.actions[this.actions.length] = action;
    };
    this.clearActions = function () {
        this.actions = [];
    };
    this.findElement = function (locator) {
        return new RecordingWebElement(locator);
    }
}

TestSession =  {
    data: [],
    current: function () {
        return this;
    },
    getReport: function () {
        return {
            info: function (name) {
                var entry = {name: name};
                TestSession.data.push(entry);
                return {
                    withDetails: function (details) {
                        entry.details = details;
                    }
                };
            }
        }
    }
};


describe("GalenPages", function (){
    describe("#convertTimeToMillis", function () {
        it("should convert time from string", function () {
            var data = [
                ["1000", 1000],
                ["1m", 60000],
                ["1 m", 60000],
                ["2m", 120000],
                ["1s", 1000],
                ["10 s", 10000]
            ];

            for (var i = 0; i<data.length; i++) {
                var realValue = GalenPages.convertTimeToMillis(data[i][0]);
                assertThat("Check #" + i + " should be", realValue).is(data[i][1]);
            }
        });
    });

    describe("#wait", function () {
        it("should throw error if waiting for nothing", function () {
            assertError(function () {
               GalenPages.wait({time: 4000, period: 1000}).untilAll({});
            }).is("You are waiting for nothing");
        });
        it("should allow to wait in milliseconds for multiple named conditions", function () {
            var counter1 = 0, counter2 = 0, counter3 = 0;
            GalenPages.wait({time: 4000, period: 1000}).untilAll({
                "Element 1": function () { counter1++; return true;},
                "Element 2": function () { counter2++; return true;},
                "Element 3": function () { counter3++; return true;}
            });

            counter1.should.equal(1);
            counter2.should.equal(1);
            counter3.should.equal(1);
        });
        it("should allow to wait in minutes and seconds", function () {
            var counter = 0;

            assertError(function () {
                GalenPages.wait({time: "2m", period: "30s"}).untilAll({
                    "Element 1": function () { counter++; return false;}
                });
            }).is("timeout error waiting for:\n  - Element 1");

            counter.should.equal(5);
        }),
        it("should throw error with all failing user defined messages", function () {

            var counter1 = 0, counter2 = 0, counter3 = 0;

            assertError(function () {
                GalenPages.wait({time: 4000, period: 2000}).untilAll({
                    "Element 1": function () { counter1++; return true;},
                    "Element 2": function () { counter2++; return false;},
                    "Element 3": function () { counter3++; return false;}
                });
            }).is("timeout error waiting for:\n  - Element 2\n  - Element 3");

            counter1.should.equal(3);
            counter2.should.equal(3);
            counter3.should.equal(1);
        });
        it("should allow to wait forEach element in array", function () {
            var elements = [
                {calls: 0, isDisplayed: function (){this.calls++; return true;}},
                {calls: 0, isDisplayed: function (){this.calls++; return false;}},
                {calls: 0, isDisplayed: function (){this.calls++; return false;}}
            ];

            assertError(function (){
                GalenPages.wait({time: "10s"}).forEach(elements, "test element should be visible", function (element) {
                    return element.isDisplayed();
                });
            }).is("timeout error waiting for:\n  - #2 test element should be visible\n  - #3 test element should be visible");

            assertThat("Item 0 calls is ", elements[0].calls).is(11);
            assertThat("Item 1 calls is ", elements[1].calls).is(11);
            assertThat("Item 2 calls is ", elements[2].calls).is(1);
        });
    });

    describe("#create", function (){
        it("should return wrapped instance of webdriver", function() {
            var gjs = GalenPages.create(dummyDriver);

            should.exist(gjs);
            should.exist(gjs.driver);
            gjs.driver.should.equal(dummyDriver);

            should.exist(gjs.page);
            should.exist(gjs.component);
        });
    });

    describe("#page", function () {
        var $ = GalenPages.create(dummyDriver);
        it("should create a simple page", function () {
            var page = $.page();
            should.exist(page);
            should.exist(page.driver);
            should.exist(page.findChild);
            should.exist(page.findChildren);

            page.driver.should.equal(dummyDriver);
        });

        it("should create a simple page and replace properties", function () {
            var page = $.page().set({
                someProperty: "some value"
            });
            should.exist(page);
            should.exist(page.driver);
            should.exist(page.findChild);
            should.exist(page.findChildren);
            page.driver.should.equal(dummyDriver);
            should.exist(page.someProperty);
            assertThat("somePropery of page should be equal", page.someProperty).is("some value");
        });

        it("should create a page with fields and process id css xpath locators", function (){
            var page = $.page({
                label: ".some label",
                link: "id:  some-link",
                button: "xpath: //some-button"
            });

            should.exist(page.label);
            should.exist(page.link);
            should.exist(page.button);

            toJson(page.label.locator).should.equal(toJson({type: "css", value: ".some label"}));
            toJson(page.link.locator).should.equal(toJson({type: "id", value: "some-link"}));
            toJson(page.button.locator).should.equal(toJson({type: "xpath", value: "//some-button"}));
        });

        it("should create a page and use functions as-is with fields", function () {
            var page = $.page({
                label: ".some label",
                doIt: function (a) {
                    var b = a +5;
                    return "result is: " + b;
                }
            });

            var returnedValue = page.doIt(3);
            returnedValue.should.equal("result is: 8");
        });

        it("should create a page and split fields into main and secondary", function () {
            var page = $.page({
                mainField1: ".some-field-1",
                mainField2: ".some-field-1"
            }, {
                secondaryField1: ".secondary-field-1",
                secondaryField2: ".secondary-field-2"
            });

            assertThat("Primary fields should be", page.primaryFields).is(["mainField1", "mainField2"]);

            should.exist(page.secondaryField1);
            should.exist(page.secondaryField2);
        });

        it("should create page elements with all needed functions", function (){
            var page = $.page({
                someField: ".some-field"
            });

            assertThat("page.someField", page.someField).hasFields([
                "click", "typeText", "clear", "isDisplayed", "getWebElement"
            ]);
            assertThat("page.someField.click type should be", page.someField.click).typeIs("function");
            assertThat("page.someField.typeText type should be", page.someField.typeText).typeIs("function");
            assertThat("page.someField.clear type should be", page.someField.clear).typeIs("function");
            assertThat("page.someField.isDisplayed type should be", page.someField.isDisplayed).typeIs("function");
            assertThat("page.someField.getWebElement type should be", page.someField.getWebElement).typeIs("function");
        });
    });

    describe("#extendPage", function () {
        it("should extend page object with page elements", function () {
            var MyPage = function (driver) {
                GalenPages.extendPage(this, driver, {
                    someField: ".some-field",
                    someFunc: function () {
                        return "some value";
                    }
                });
            };

            var driver = new RecordingDriver();
            var myPage = new MyPage(driver);

            assertThat("myPage.someField", myPage.someField).hasFields([
                "click", "typeText", "clear", "isDisplayed", "getWebElement"
            ]);
        });
    });

    describe("page elements interaction", function () {
        var driver = new RecordingDriver();
        var $ = GalenPages.create(driver);

        it("should trigger getWebElement only once when doing actions on it", function (){
            driver.clearActions();
            var page = $.page({someField: ".some-field"});
            page.someField.click();
            page.someField.typeText("Some text");
            page.someField.clear();
            page.someField.isDisplayed();
            page.someField.getWebElement();
            page.someField.isEnabled();
            page.someField.attribute("someattr");
            page.someField.cssValue("display");

            assertThat("Performed actions on driver should be", driver.actions).is(["#findElement {\"t\":\"css\",\"v\":\".some-field\"}"]);

            assertThat("Performed actions on web element should be", page.someField.getWebElement().actions).is([
                "#click",
                "#sendKeys Some text",
                "#clear",
                "#isDisplayed",
                "#isEnabled",
                "#getAttribute someattr",
                "#getCssValue display"
            ]);
        });

        it("should report all events", function (){
            driver.clearActions();
            var page = $.page({someField: ".some-field"});

            GalenPages.settings.allowReporting = true;
            TestSession.data = [];

            page.someField.click();
            page.someField.typeText("Some text");
            page.someField.clear();
            page.someField.isDisplayed();
            page.someField.getWebElement();
            page.someField.isEnabled();
            page.someField.attribute("someattr");
            page.someField.cssValue("display");
            page.someField.selectByValue("blahblah");
            page.someField.selectByText("blahblah");

            assertThat("TestSession report should be", TestSession.data).is([
                {name: "Click someField", details: "css: .some-field"},
                {name: "Type text \"Some text\" to someField", details: "css: .some-field"},
                {name: "Clear someField", details: "css: .some-field"},
                {name: "Select by value \"blahblah\" in someField", details: "css: .some-field"},
                {name: "Select by text \"blahblah\" in someField", details: "css: .some-field"},
            ]);
        });

        it("should not report if reporting is disabled", function (){
            driver.clearActions();
            var page = $.page({someField: ".some-field"});

            GalenPages.settings.allowReporting = false;
            TestSession.data = [];

            page.someField.click();
            page.someField.typeText("Some text");
            page.someField.clear();
            page.someField.isDisplayed();
            page.someField.getWebElement();
            page.someField.isEnabled();
            page.someField.attribute("someattr");
            page.someField.cssValue("display");
            page.someField.selectByValue("blahblah");
            page.someField.selectByText("blahblah");

            assertThat("TestSession report should be", TestSession.data).is([]);
        });

        it("should handle NoSuchElementException from java", function () {
            driver.clearActions();
            driver.findElement = function (){throw new Error("No Such element");}
            var page = $.page({someField: ".some-field"});

            assertError(function (){
                page.someField.typeText("Some text");
            }).is("No such element: css .some-field");

            assertError(function (){
                page.someField.click();
            }).is("No such element: css .some-field");

            assertError(function (){
                page.someField.clear();
            }).is("No such element: css .some-field");

            assertThat("'exists' should give", page.someField.exists()).is(false);
        });
    });

    describe("page waiting", function () {
        var driver = new RecordingDriver();
        var $ = GalenPages.create(driver);

        it("should wait for primaryFields only", function () {
            driver.clearActions();
            var page = $.page({
                label: ".some-field",
                button: ".some-button"
            }, {
                label2: ".some-label2"
            });

            page.label.counter = 0;
            page.label.exists = function () {
                this.counter = this.counter + 1;
                return this.counter > 3;
            };

            page.label2.exists = function () {return false};

            page.waitForIt();

            page.label.counter.should.equal(4);
        });

        it("should throw error if a field is not displayed", function (){
            driver.clearActions();
            var page = $.page({
                label: ".some-field",
                button: ".some-button"
            });

            page.label.counter = 0;
            page.label.exists = function () {
                this.counter = this.counter + 1;
                return false;
            };

            assertError(function (){
                page.waitForIt();
            }).is("timeout waiting for page elements:\n  - label to be displayed");

            page.label.counter.should.equal(11);
        });
    });

    describe("#component", function (){
        var driver = new RecordingDriver();
        var $ = GalenPages.create(driver);

        it("should create component", function () {
            var c = $.component({
                label: ".some-label",
                someFunction: function (){}
            });

            should.exist(c.driver);
            should.exist(c.label);
            should.exist(c.label.locator);
            should.exist(c.someFunction);
            should.exist(c.waitForIt);

            assertThat("Typeof someFunction", typeof c.someFunction).is("function");
        });

    });

    describe("basic functions", function (){
        var driver = new RecordingDriver();
        var $ = GalenPages.create(driver);

        describe("#get", function (){
            it("should load new page", function (){
                driver.clearActions();
                $.get("http://example.com");
                assertThat("Driver actions should be", driver.actions).is(["#get http://example.com"]);
            });
        });

        describe("#refresh", function () {
            it("should invoke driver.navigate().reload()", function (){
                driver.clearActions();
                $.refresh();
                assertThat("Driver actions should be", driver.actions).is(["#navigate().reload"]);
            });
        });

        describe("#back", function () {
            it("should invoke driver.navigate().back()", function () {
                driver.clearActions();
                $.back();
                assertThat("Driver actions", driver.actions).is(["#navigate().back"]);
            });
        });

        describe("#currentUrl", function () {
            it("should return current url in browser", function (){
                driver.clearActions();
                var url = $.currentUrl();
                assertThat("Driver actions", driver.actions).is(["#getCurrentUrl"]);
                assertThat("The returned url", url).is("http://fakeurl.fake");
            });
        });

        describe("#pageSource", function () {
            it("should return page source from driver", function (){
                driver.clearActions();
                var pageSource = $.pageSource();
                assertThat("Driver actions", driver.actions).is(["#getPageSource"]);
                assertThat("Page source", pageSource).is("<fake>page source</fake>");
            });
        });

        describe("#title", function () {
            it("should return title from driver", function () {
                driver.clearActions();
                var title = $.title();
                assertThat("Driver actions", driver.actions).is(["#getTitle"]);
                assertThat("Title", title).is("Fake title");
            });
        });
    });
});
