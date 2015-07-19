importClass(com.galenframework.components.JsTestRegistry);


test("Test number 1", function () {
    JsTestRegistry.get().registerEvent("Test #1 was invoked");
});

test("Test number 2", function () {
    JsTestRegistry.get().registerEvent("Test #2 was invoked");
    throw new Exception("Some error");
});

test("Test number 3", function () {
    JsTestRegistry.get().registerEvent("Test #3 was invoked");
});
