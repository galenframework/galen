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
    registerFunction: function (name) {
        return function() {
            AssertEvents.say(name, Array.from(arguments));
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
