var _ = require("./../../main/resources/js/GalenApi.js"),
    assertThat = require("./assertThat.js").assertThat,
    assertError = require("./assertThat.js").assertError;

GlobalEvents = {
    events: [],
    say: function (name, args) {
        this.events.push({
            name: name,
            args: args
        });
    },
    clearEvents: function () {
        this.events = [];
    },
    registerFunction: function (name, argumentsAmount) {
        return function(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) {
            var args = [arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10].splice(0, argumentsAmount);
            GlobalEvents.say(name, args);
        };
    }
};
registerFunction = function(name, argumentsAmount) {
    return GlobalEvents.registerFunction(name, argumentsAmount);
};

assertEvents = function (callback) {
    GlobalEvents.clearEvents();
    callback();
    var events = GlobalEvents.events;

    return {
        shouldBe: function (expectedEvents){
            assertThat("Events should be", events).is(expectedEvents);
        }
    };
};



GalenJsApi = {
    checkLayout: registerFunction("GalenJsApi.checkLayout", 6),
    resizeDriver: registerFunction("GalenJsApi.resizeDriver", 2)
};

describe("GalenJsApi", function () {
    describe("#checkLayout", function () {
        it("should call GalenJsApi.checkLayout", function () {
            assertEvents(function () {
                _.checkLayout("driver", "page.spec", ["mobile", "desktop"], ["nomobile"]);
            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver", "page.spec", ["mobile", "desktop"], ["nomobile"], null, null]
            }]);
        });

        it("should convert tags single string to array", function () {
            assertEvents(function () {
                _.checkLayout("driver", "page.spec", "mobile", "nomobile");
            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver", "page.spec", ["mobile"], ["nomobile"], null, null]
            }]);
        });

        it("should call GalenJsApi.checkLayout when calling checkLayout with single argument", function () {
            assertEvents(function () {
                _.checkLayout({
                    driver: "driver1",
                    spec: "page.spec",
                    tags: ["mobile"],
                    excludedTags: ["nomobile"],
                    screenshot: "screenshotFile.png"
                });

            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver1", "page.spec", ["mobile"], ["nomobile"], null, "screenshotFile.png"]
            }]);

        });
    });

    describe("#resize", function () {
        it("should call GalenJsApi.resize", function () {
            assertEvents(function () {
                _.resize({d: "driver"}, "1024x768");
            }).shouldBe([{
                name: "GalenJsApi.resizeDriver",
                args: [{d: "driver"}, "1024x768"]
            }]);
        });
    });
});

