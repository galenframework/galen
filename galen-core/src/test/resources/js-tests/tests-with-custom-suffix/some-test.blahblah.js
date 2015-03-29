importClass(net.mindengine.galen.components.JsTestRegistry);


test("Test number 1", function () {
    JsTestRegistry.get().registerEvent("Test #1 was invoked");
});


