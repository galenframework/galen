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



GalenUtils = {
    checkLayout: registerFunction("GalenUtils.checkLayout", 4)
};

describe("GalenUtils", function () {
    describe("#checkLayout", function () {
        it("should call GalenUtils.checkLayout", function () {
            assertEvents(function () {
                _.checkLayout("driver", "page.spec", ["mobile", "desktop"], ["nomobile"]);
            }).shouldBe([{
                name: "GalenUtils.checkLayout",
                args: ["driver", "page.spec", ["mobile", "desktop"], ["nomobile"]]
            }]);
        });

        it("should convert tags single string to array", function () {
            assertEvents(function () {
                _.checkLayout("driver", "page.spec", "mobile", "nomobile");
            }).shouldBe([{
                name: "GalenUtils.checkLayout",
                args: ["driver", "page.spec", ["mobile"], ["nomobile"]]
            }]);
        });

    }); 
});

