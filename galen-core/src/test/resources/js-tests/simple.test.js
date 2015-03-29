importClass(net.mindengine.galen.components.JsTestRegistry);


test("Test number 1", function () {
    JsTestRegistry.get().registerEvent("Test #1 was invoked");
});


test("Test number 2", function () {
    JsTestRegistry.get().registerEvent("Test #2 was invoked");
});


test("Test number 3", function () {
    JsTestRegistry.get().registerEvent("Test #3 was invoked");
});


