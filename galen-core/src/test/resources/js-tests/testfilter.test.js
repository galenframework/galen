importClass(com.galenframework.components.JsTestRegistry);



test("Test A", function () {
    JsTestRegistry.get().registerEvent("Test A invoked");
});
test("Test B", function () {
    JsTestRegistry.get().registerEvent("Test B invoked");
});
test("Test C", function () {
    JsTestRegistry.get().registerEvent("Test C invoked");
});
test("Test D", function () {
    JsTestRegistry.get().registerEvent("Test D invoked");
});


testFilter(function (tests) {
    var newTests = [];
    for (var i = tests.length - 1; i >= 0; i--) {
        if (tests[i].name != "Test B") {
            newTests.push(tests[i]);
        }
    }
    return newTests;
});