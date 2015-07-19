importClass(com.galenframework.components.JsTestRegistry);


grouped("mobile", function () {

    test("Test A", function (){
        JsTestRegistry.get().registerEvent("Test A invoked");
    });

    grouped(["tablet", "desktop"], function () {
        test("Test B", function (){
            JsTestRegistry.get().registerEvent("Test B invoked");
        });

        test("Test C", function (){
            JsTestRegistry.get().registerEvent("Test C invoked");
        });
    });

});


test("Test D", function () {
    JsTestRegistry.get().registerEvent("Test D invoked");
});