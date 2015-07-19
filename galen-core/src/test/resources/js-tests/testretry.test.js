importClass(com.galenframework.components.JsTestRegistry);

beforeTestSuite(function () {
   JsTestRegistry.get().registerEvent("Before test suite event");
});

afterTestSuite(function () {
   JsTestRegistry.get().registerEvent("After test suite event");
});

beforeTest(function (test) {
   JsTestRegistry.get().registerEvent("Before test event for: " + test.getName());
});

afterTest(function (test) {
   JsTestRegistry.get().registerEvent("After test event for: " + test.getName());
});

testRetry(function (test, retryCount){
   JsTestRegistry.get().registerEvent("Retry handler invoked for test: " + test.getName());

   // Retrying only Test A
   if (retryCount < 3) {
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
test("Test C", function () {
   JsTestRegistry.get().registerEvent("Test C invoked");
});
