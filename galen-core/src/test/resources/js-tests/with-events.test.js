importClass(com.galenframework.components.JsTestRegistry);

test("Test number 1", function () {
    JsTestRegistry.get().registerEvent("Test #1 was invoked");
});


test("Test number 2", function () {
    JsTestRegistry.get().registerEvent("Test #2 was invoked");
});




beforeTest(function (test) {
    JsTestRegistry.get().registerEvent("Before test: " + test.getName());
});

afterTest(function (test) {
    JsTestRegistry.get().registerEvent("After test: " + test.getName());
});

beforeTestSuite(function () {
    JsTestRegistry.get().registerEvent("Before test suite");
});

afterTestSuite(function () {
    JsTestRegistry.get().registerEvent("After test suite");
});