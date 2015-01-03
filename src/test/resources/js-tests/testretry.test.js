importClass(net.mindengine.galen.components.JsTestRegistry);

testRetry(function (test, retryCount){
   JsTestRegistry.get().registerEvent("Retry handler invoked for test: " + test.getName());

   // Retrying only Test A
   if (test.getName() == "Test A" && retryCount < 3) {
      return true;
   }
   else {
      return false;
   }
});



test("Test A", function () {
   JsTestRegistry.get().registerEvent("Test A invoked");
   throw new Error("");
});
test("Test B", function () {
   JsTestRegistry.get().registerEvent("Test B invoked");
   throw new Error("");
});