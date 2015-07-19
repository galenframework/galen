importClass(com.galenframework.components.JsTestRegistry);


test("Test number 2", function () {
    JsTestRegistry.get().registerEvent("Test #2 was invoked");
});
