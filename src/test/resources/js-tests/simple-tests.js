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

_galenCore.addTest(new GalenTest({
    getName: function () {
        return "Test number 3";
    },
    execute: function (report, listener) {
        JsTestRegistry.get().registerEvent("Test #3 was invoked");
    }
}));