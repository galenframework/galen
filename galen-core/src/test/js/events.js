var assertThat = require("./assertThat.js").assertThat;

var AssertEvents = {
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
            AssertEvents.say(name, args);
        };
    },
    assert: function(callback) {
        AssertEvents.clearEvents();
        callback();
        var events = AssertEvents.events;

        return {
            shouldBe: function (expectedEvents){
                assertThat("Events should be", events).is(expectedEvents);
            }
        };
    }
};

(function (exports) {
    exports.AssertEvents = AssertEvents;
})(this);
