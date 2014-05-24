importClass(net.mindengine.galen.components.JsTestRegistry);



_galenCore.addTest(new GalenTest({
    getName: function () {
        return "Test number 1";
    },
    execute: function (report, listener) {
        JsTestRegistry.get().registerEvent("Test #1 was invoked");
    }
}));

_galenCore.addTest(new GalenTest({
    getName: function () {
        return "Test number 2";
    },
    execute: function (report, listener) {
        JsTestRegistry.get().registerEvent("Test #2 was invoked");
    }
}));




_galenCore.addBeforeTestEvent(new TestEvent({
    execute: function (test) {
        JsTestRegistry.get().registerEvent("Before test: " + test.getName());
    }
}));

_galenCore.addAfterTestEvent(new TestEvent({
    execute: function (test) {
        JsTestRegistry.get().registerEvent("After test: " + test.getName());
    }
}));

_galenCore.addBeforeTestSuiteEvent(new TestSuiteEvent({
    execute: function () {
        JsTestRegistry.get().registerEvent("Before test suite");
    }
}));

_galenCore.addAfterTestSuiteEvent(new TestSuiteEvent({
    execute: function () {
        JsTestRegistry.get().registerEvent("After test suite");
    }
}));