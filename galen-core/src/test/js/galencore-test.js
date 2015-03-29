var _ = require("./../../main/resources/js/GalenCore.js"),
    assertThat = require("./assertThat.js").assertThat,
    assertError = require("./assertThat.js").assertError;

//mocking GalenJsTest
GalenTest = function(jsObject) {
    this.jsObject = jsObject;
};

TestSuiteEvent = function (jsObject) {
    this.jsObject = jsObject;
};

TestEvent = function (jsObject) {
    this.jsObject = jsObject;
};

//mocking the _galenCore which should be provided by Galen Framework js executor
_galenCore = {
    tests: [],
    events: [],
    addTest: function(test) {
        this.tests.push(test);
    },
    addBeforeTestSuiteEvent: function (event) {
        event.jsObject.type = "beforeTestSuite";
        this.events.push(event);
    },
    addAfterTestSuiteEvent: function (event) {
        event.jsObject.type = "afterTestSuite";
        this.events.push(event);
    },
    addBeforeTestEvent: function (event) {
        event.jsObject.type = "beforeTest";
        this.events.push(event);
    },
    addAfterTestEvent: function (event) {
        event.jsObject.type = "afterTest";
        this.events.push(event);
    }
};

function executeAllTests() {
    for (var i = 0; i < _galenCore.tests.length; i++) {
        _galenCore.tests[i].jsObject.execute();
    }
}

describe("GalenCore", function () {
    describe("#test", function () {
        it("should create a simple test and add to GalenCore", function () {
            _galenCore.tests = [];
            _.test("Home page test", function () {
                return "Some test result";
            });

            assertThat("Amount of global tests should be", _galenCore.tests.length).is(1);
            assertThat("The callbacks amount should be", _galenCore.tests[0].jsObject.callbacks.length).is(1);
            assertThat("The callback should return", _galenCore.tests[0].jsObject.callbacks[0]()).is("Some test result");

        });

        it("should create a test with one argument", function () {
            var aTest = _.test("Home page test").on("one argument", function (arg) {
                return "result with arg " + arg;
            });

            assertThat("A test should have name", aTest.testName).is("Home page test");
            assertThat("A test should have amount of arguments", aTest.arguments.length).is(1);
            assertThat("A test argument should be", aTest.arguments[0]).is("one argument");
            assertThat("A test should have one callback", aTest.callbacks.length).is(1);
            assertThat("A test callback should return", aTest.callbacks[0]("blah")).is("result with arg blah");
        });

        it("should create a test with multiple arguments", function () {
            var aTest = _.test("Some test").on([1, "2", "3"], function (arg1, arg2, arg3) {
            });

            assertThat("A test should have arguments array", aTest.arguments).is([1, "2", "3"]);
        });

    });

    describe("#forAll", function (){
        it("should create parameterization with arrays", function (){
            var invokations = [];
            _.forAll([  ["1.1", "1.2"],
                            ["2.1", "2.2"]
            ], function (arg1, arg2) {
                invokations.push(arg1 + ", " + arg2);
            });

            assertThat("Invokations should be", invokations).is(["1.1, 1.2", "2.1, 2.2"]);
        });

        it("should allow up to 16 args for arrays parameterization", function () {
            var args = [];
            _.forAll([  ["1", "2", "3", "4", "5", "6", "7", "8", "9", '10', '11', '12', '13', '14', '15', '16']
            ], function (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) {
                args = [a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16];
            });

            assertThat("Invokations should be", args).is(["1", "2", "3", "4", "5", "6", "7", "8", "9", '10', '11', '12', '13', '14', '15', '16']);
        });

        it("should parameterize using structures", function () {
            var invokations = [];

            _.forAll({
                mobile: {
                    size: "400x800"
                },
                tablet: {
                    size: "600x800"
                },
                desktop: {
                    size: "1024x768"
                }
            }, function (value){
                invokations.push(value);
            });


            assertThat("Invokations should be", invokations).is([
                {size: "400x800"},
                {size: "600x800"},
                {size: "1024x768"}
            ]);
        });

        it("should allow to parameterize tests", function () {
            _galenCore.tests = [];
            _.forAll([["1"], ["2"], ["3"]], function (arg) {
                _.test("Test number #" + arg, function() {
                });
            });

            assertThat("Should have number of tests", _galenCore.tests.length).is(3);
            assertThat("Test 1 name should be", _galenCore.tests[0].jsObject.testName).is("Test number #1");
            assertThat("Test 2 name should be", _galenCore.tests[1].jsObject.testName).is("Test number #2");
            assertThat("Test 3 name should be", _galenCore.tests[2].jsObject.testName).is("Test number #3");
        });


        it("should provide parameters to test using arrays", function () {
            var testValue = [];
            var index = _galenCore.tests.length;

            _.forAll([["1"], ["2"], ["3"]], function () {
                _.test("My test", function (arg) {
                    testValue.push("Value from test: " + arg);
                });
            });

            for (var i=index;i<index + 3; i++) {
                _galenCore.tests[i].jsObject.execute();
            }

            assertThat("Callback results should be", testValue).is([
                "Value from test: 1",
                "Value from test: 2",
                "Value from test: 3"
            ]);
        });

        it("should provide parameters to test using structures", function () {
            var testValue = [];
            var index = _galenCore.tests.length;

            _.forAll({
                mobile: {name: "mobile"},
                tablet: {name: "tablet"},
                desktop: {name: "desktop"}
            },function () {
                _.test("Test structures arguments stack", function (arg) {
                    testValue.push("Value from test: " + arg.name);
                });
            });

            for (var i = index; i < index + 3; i++) {
                _galenCore.tests[i].jsObject.execute();
            }


            assertThat("Callback results should be", testValue).is([
                "Value from test: mobile",
                "Value from test: tablet",
                "Value from test: desktop"
            ]);
        });

        it("should allow test to use simple templating in test name", function () {
            var tests = [];

            _.forAll({
                mobile: {deviceName: "mobile", size: "400x800"},
                tablet: {deviceName: "tablet", size: "640x480"}
            }, function () {
                tests.push(_.test("Home page on ${deviceName} device with size ${size}", function () {}));
            });

            assertThat("Test #1 name should be", tests[0].testName).is("Home page on mobile device with size 400x800");
            assertThat("Test #2 name should be", tests[1].testName).is("Home page on tablet device with size 640x480");
        });

        it("should allow test to use simple templating in test name with multi-level parameterization", function () {
            var testNames = [];

            _.forAll({
                linux: {
                    osName: "linux"
                },
                windows: {
                    osName: "windows"
                }
            }, function () {
                _.forAll({
                    mobile: {deviceName: "mobile", size: "400x800"},
                    tablet: {deviceName: "tablet", size: "640x480"}
                }, function () {
                    testNames.push(_.test("Home page on ${deviceName} device with size ${size} on ${osName} operating system", function () {}).testName);
                });

            });

            assertThat("Test names should be", testNames).is([
                "Home page on mobile device with size 400x800 on linux operating system",
                "Home page on tablet device with size 640x480 on linux operating system",
                "Home page on mobile device with size 400x800 on windows operating system",
                "Home page on tablet device with size 640x480 on windows operating system"
            ]);
        });


        it("should provide all arguments from all levels of parameterization with arrays", function () {
            //setup
            _galenCore.tests = [];
            invokations = [];

            _.GalenCore.settings.parameterization.stackBackwards = false;

            //action
            _.forAll([
                ["linux", "12"],
                ["windows", "xp"]
            ], function () {
                _.forAll([
                    ["firefox", "11"],
                    ["chrome", "25"]
                ], function () {
                    _.forAll([
                        ["mobile", "400x800"],
                        ["tablet", "800x600"]
                    ], function () {
                        _.test("Home page test", function (osName, osVersion, browser, browserVersion, device, size) {
                            invokations.push([osName, osVersion, browser, browserVersion, device, size]);
                        });
                    });
                });
            });

            executeAllTests();

            assertThat("Invokations should be", invokations).is([
                ["linux", "12", "firefox", "11", "mobile", "400x800"],
                ["linux", "12", "firefox", "11", "tablet", "800x600"],
                ["linux", "12", "chrome", "25", "mobile", "400x800"],
                ["linux", "12", "chrome", "25", "tablet", "800x600"],
                ["windows", "xp", "firefox", "11", "mobile", "400x800"],
                ["windows", "xp", "firefox", "11", "tablet", "800x600"],
                ["windows", "xp", "chrome", "25", "mobile", "400x800"],
                ["windows", "xp", "chrome", "25", "tablet", "800x600"]
            ]);
        });

        it("should provide all arguments backwards (on settings) from all levels of parameterization with arrays", function () {
            //setup
            _galenCore.tests = [];
            invokations = [];

            _.GalenCore.settings.parameterization.stackBackwards = true;

            //action
            _.forAll([
                ["linux", "12"],
                ["windows", "xp"]
            ], function () {
                _.forAll([
                    ["firefox", "11"],
                    ["chrome", "25"]
                ], function () {
                    _.forAll([
                        ["mobile", "400x800"],
                        ["tablet", "800x600"]
                    ], function () {
                        _.test("Home page test", function (device, size, browser, browserVersion, osName, osVersion) {
                            invokations.push([device, size, browser, browserVersion, osName, osVersion]);
                        });
                    });
                });
            });

            executeAllTests();

            assertThat("Invokations should be", invokations).is([
                ["mobile", "400x800", "firefox", "11", "linux", "12"],
                ["tablet", "800x600", "firefox", "11", "linux", "12"],
                ["mobile", "400x800", "chrome", "25", "linux", "12"],
                ["tablet", "800x600", "chrome", "25", "linux", "12"],
                ["mobile", "400x800", "firefox", "11", "windows", "xp"],
                ["tablet", "800x600", "firefox", "11", "windows", "xp"],
                ["mobile", "400x800", "chrome", "25", "windows", "xp"],
                ["tablet", "800x600", "chrome", "25", "windows", "xp"]
            ]);
        });


        it("should provide all arguments from all levels of parameterization with structures", function () {
            //setup
            _galenCore.tests = [];
            invokations = [];

            _.GalenCore.settings.parameterization.stackBackwards = false;

            //action
            _.forAll({
                linux: {name: "linux"},
                windows: {name: "windows"}
            }, function () {
                _.forAll({
                    firefox: {name: "firefox"},
                    chrome: {name: "chrome"}
                }, function () {
                    _.forAll({
                        mobile: {deviceName: "mobile", size: "400x800"},
                        tablet: {deviceName: "tablet", size: "800x600"}
                    }, function () {
                        _.test("Home page test ${deviceName} ${size}", function (os, browser, device) {
                            invokations.push([this.testName, os, browser, device]);
                        });
                    });
                });
            });

            executeAllTests();

            assertThat("Invokations should be", invokations).is([
                ["Home page test mobile 400x800", {name: "linux"}, {name: "firefox"}, {deviceName: "mobile", size: "400x800"}],
                ["Home page test tablet 800x600", {name: "linux"}, {name: "firefox"}, {deviceName: "tablet", size: "800x600"}],
                ["Home page test mobile 400x800", {name: "linux"}, {name: "chrome"}, {deviceName: "mobile", size: "400x800"}],
                ["Home page test tablet 800x600", {name: "linux"}, {name: "chrome"}, {deviceName: "tablet", size: "800x600"}],
                ["Home page test mobile 400x800", {name: "windows"}, {name: "firefox"}, {deviceName: "mobile", size: "400x800"}],
                ["Home page test tablet 800x600", {name: "windows"}, {name: "firefox"}, {deviceName: "tablet", size: "800x600"}],
                ["Home page test mobile 400x800", {name: "windows"}, {name: "chrome"}, {deviceName: "mobile", size: "400x800"}],
                ["Home page test tablet 800x600", {name: "windows"}, {name: "chrome"}, {deviceName: "tablet", size: "800x600"}]
            ]);
        });
    });

    describe("#forOnly", function () {
        it("should generate test only once using arrays", function () {
            _galenCore.tests = [];

            var invokations = [];
            _.forOnly(["mobile", "400x800"], function () {
                _.test("Test", function (device, size) {
                    invokations.push([device, size]);
                });
            });

            executeAllTests();
            assertThat("Invokations should be", invokations).is([["mobile", "400x800"]]);
        });

        it("should generate test only once using object", function () {
            _galenCore.tests = [];

            var invokations = [];
            _.forOnly({
                name: "mobile",
                size: "400x800"
            }, function () {
                _.test("Test", function (device) {
                    invokations.push(device);
                });
            });

            executeAllTests();
            assertThat("Invokations should be", invokations).is([{
                name: "mobile",
                size: "400x800"
            }]);
        });
    });

    describe("#beforeTestSuite", function () {
        it("should add a simple callback to the galenCore", function () {
            _galenCore.events = [];

            var invoked = false;
            _.beforeTestSuite(function () {
                invoked = true;
            });
            assertThat("Should have only one callback", _galenCore.events.length).is(1);
            assertThat("Event type should be", _galenCore.events[0].jsObject.type).is("beforeTestSuite");

            _galenCore.events[0].jsObject.execute();
            assertThat("Callback should be invoked", invoked).is(true);
        });
    });

    describe("#afterTestSuite", function () {
        it("should add a simple callback to the galenCore", function () {
            _galenCore.events = [];
            var invoked = false;
            _.afterTestSuite(function () {
                invoked = true;
            });
            assertThat("Should have only one callback", _galenCore.events.length).is(1);
            assertThat("Event type should be", _galenCore.events[0].jsObject.type).is("afterTestSuite");

            _galenCore.events[0].jsObject.execute();
            assertThat("Callback should be invoked", invoked).is(true);
        });
    });

    describe("#beforeTest", function () {
        it("should add a callback to the galenCore", function () {
            _galenCore.events = [];
            var invoked = false;
            _.beforeTest(function (test) {
                invoked = true;
            });

            assertThat("Should have only one callback", _galenCore.events.length).is(1);
            assertThat("Event type should be", _galenCore.events[0].jsObject.type).is("beforeTest");
            _galenCore.events[0].jsObject.execute({});
            assertThat("Callback should be invoked", invoked).is(true);
        });
    });

    describe("#afterTest", function () {
        it("should add a callback to the galenCore", function () {
            _galenCore.events = [];
            var invoked = false;
            _.afterTest(function (test, args) {
                invoked = true;
            });

            assertThat("Should have only one callback", _galenCore.events.length).is(1);
            assertThat("Event type should be", _galenCore.events[0].jsObject.type).is("afterTest");
            _galenCore.events[0].jsObject.execute({});
            assertThat("Callback should be invoked", invoked).is(true);
        });
    });

    describe("#retry", function () {
        it("should throw exception in case all tries are out", function () {
            var tries = 4;

            var invokations = [];

            assertError(function () {
                _.retry(3, function (triesLeft) {
                    invokations.push("Tries left: " + triesLeft)
                    tries = tries - 1;
                    throw new Error("This was try #" + tries);
                });
            }).is("This was try #0");

            assertThat("Invokations should be", invokations).is([
                "Tries left: 3",
                "Tries left: 2",
                "Tries left: 1",
                "Tries left: 0"
            ]);

        });

        it("should not thow exception and run only once if callback is successfull", function (){
            var tries = 4;
            _.retry(3, function () {
                tries = tries - 1;
            });

            assertThat("tries should be", tries).is(3);
        });
    });

    describe("dataProviders", function () {
        it("should provide data to tests", function () {
            var featureSwitched = _.createTestDataProvider("featureSwitches");

            _galenCore.tests = [];

            var invokationData = [];

            featureSwitched("F1", function () {
                featureSwitched("F2", function () {
                    _.test("Test with two features", function () {
                        invokationData.push({
                            data: this.data.featureSwitches
                        });
                    });
                });

                featureSwitched("F3", function () {
                    _.test("Another test with two features", function () {
                        invokationData.push({
                            data: this.data.featureSwitches
                        });
                    });
                });

                _.test("Test with one feature", function () {
                    invokationData.push({
                        data: this.data.featureSwitches
                    });
                });
            });

            _.test("Test without any features", function () {
                invokationData.push({
                    data: this.data.featureSwitches
                });
            });

            executeAllTests();

            assertThat("Invokation data should be", invokationData).is([
                {data: ["F1", "F2"]},
                {data: ["F1", "F3"]},
                {data: ["F1"]},
                {}
            ]);
        });

        it("should flatten the array values", function () {
            var featureSwitched = _.createTestDataProvider("featureSwitches");

            _galenCore.tests = [];

            var invokationData = [];

            featureSwitched("F1", function () {
                featureSwitched(["F2", "F3"], function () {
                    _.test("Test with 3 features", function () {
                        invokationData.push({
                            data: this.data.featureSwitches
                        });
                    });
                });
            });

            executeAllTests();

            assertThat("Invokation data should be", invokationData).is([
                {data: ["F1", "F2", "F3"]}
            ]);
        });
    })
});
