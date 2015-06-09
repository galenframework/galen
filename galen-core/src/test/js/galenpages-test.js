var assert = require("assert"),
    should=require("should"),
    GalenPages = require("./../../main/resources/js/GalenPages").GalenPages,
    $page = require("./../../main/resources/js/GalenPages").$page,
    $list = require("./../../main/resources/js/GalenPages").$list,
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

// Thread object is mocked in order test how galen waits for pages
Thread = {
    sleep: function (time) {
    }
};


/*
 * Mocking java list
 */
function JavaList(items) {
    this.items = items;
}
JavaList.prototype.size = function () {
    return this.items.length;
};
JavaList.prototype.get = function (index) {
    if (index >= this.size()) {
        throw new Error("Index out of bounds: " + index);
    }
    return this.items[index];
};

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
    this.findElements = function (by) {
        this.record("#findElements" + toJson(by));
        /*
        always return two mocked elements so that we can test stuff related to $list
         */
        return new JavaList([
            new RecordingWebElement(by),
            new RecordingWebElement(by)
        ]);
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


    describe("#Page", function () {
        var driver = new RecordingDriver();

        it("should create a simple page", function () {
            var page = new GalenPages.Page(driver, "some page", {});
            should.exist(page);
            should.exist(page.driver);
            should.exist(page.findChild);
            should.exist(page.findChildren);

            page.driver.should.equal(driver);
        });

        it("should create a simple page and replace properties", function () {
            var page = new GalenPages.Page(driver, "some page").set({
                someProperty: "some value"
            });
            should.exist(page);
            should.exist(page.driver);
            should.exist(page.findChild);
            should.exist(page.findChildren);
            page.driver.should.equal(driver);
            should.exist(page.someProperty);
            assertThat("somePropery of page should be equal", page.someProperty).is("some value");
        });

        it("should create a page with fields and evalSafeToString id css xpath locators", function (){
            var page = new GalenPages.Page(driver, "some page", {
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

        it("Should identify css and xpath locators based on their first character", function () {
            var page = new GalenPages.Page(driver, "some page", {
                css01: ".some label",
                css02: "#id",
                xpath01: "//some-button",
                xpath02: "/some-button"
            });

            toJson(page.css01.locator).should.equal(toJson({type: "css", value: ".some label"}));
            toJson(page.css02.locator).should.equal(toJson({type: "css", value: "#id"}));

            toJson(page.xpath01.locator).should.equal(toJson({type: "xpath", value: "//some-button"}));
            toJson(page.xpath02.locator).should.equal(toJson({type: "xpath", value: "/some-button"}));
        });

        it("should create a page and use functions as-is with fields", function () {
            var page = new GalenPages.Page(driver, "some page", {
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
            var page = new GalenPages.Page(driver, "some page", {
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
            var page = new GalenPages.Page(driver, "some page", {
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
                GalenPages.extendPage(this, driver, "My page", {
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

            assertThat("Should set a name for a page", myPage.name).is("My page");
        });
    });

    describe("page elements interaction", function () {
        var driver = new RecordingDriver();

        it("should trigger getWebElement only once when doing actions on it", function (){
            driver.clearActions();
            var page = new GalenPages.Page(driver, "some page", {someField: ".some-field"});
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
            var page = new GalenPages.Page(driver, "some page", {someField: ".some-field"});

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
                {name: "Click someField on some page", details: "css: .some-field"},
                {name: "Type \"Some text\" to someField on some page", details: "css: .some-field"},
                {name: "Clear someField on some page", details: "css: .some-field"},
                {name: "Select by value \"blahblah\" in someField on some page", details: "css: .some-field"},
                {name: "Select by text \"blahblah\" in someField on some page", details: "css: .some-field"},
            ]);
        });

        it("should not report if reporting is disabled", function (){
            driver.clearActions();
            var page = new GalenPages.Page(driver, "some page", {someField: ".some-field"});

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
            var page = new GalenPages.Page(driver, "some page", {
                someField: ".some-field"
            });

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

        it("should wait for primaryFields only", function () {
            driver.clearActions();
            var page = new GalenPages.Page(driver, "some page", {
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
            var page = new GalenPages.Page(driver, "some page", {
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


    describe("$page", function() {
        it("should create a new function with page elements", function () {
            var LoginPage = $page("Login page", {
                login: "#login",
                password: "#password",

                loginAs: function (email, password) {
                    return "Logging as " + email;
                }
            });

            var loginPage = new LoginPage(dummyDriver);
            assertThat("loginAs function should return", loginPage.loginAs("someuser@example.com", "p"))
                .is("Logging as someuser@example.com");
            should.exist(loginPage.login.locator);
            should.exist(loginPage.password.locator);
        });
    });

    describe("$list", function () {
        it("should generate a list of components", function () {
            var NoteElement = $page("Note", {
                title: ".title",
                content: ".description",

                getNoteContent: function () {
                    return "some fake content";
                }
            });

            var NotesPage = $page("Notes page", {
                title: "#title",
                notes: $list(NoteElement, "div.notes .note")
            });

            var driver = new RecordingDriver();
            var notesPage = new NotesPage(driver);

            assertThat("There should be 2 notes", notesPage.notes.size())
                .is(2);

            var secondNote = notesPage.notes.get(1);

            assertThat("Should give full name in sub components", secondNote.name)
                .is("#1 of notes on Notes page");

            assertThat("Should give full name in sub components", secondNote.title.name)
                .is("title on Note on #1 of notes on Notes page");

            assertThat("Should be able to retrieve a note", secondNote.getNoteContent())
                .is("some fake content");
        });
    });

});
