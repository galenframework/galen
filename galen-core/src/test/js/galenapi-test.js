var _ = require("./../../main/resources/js/GalenApi.js"),
    assertThat = require("./assertThat.js").assertThat,
    AssertEvents = require("./events.js").AssertEvents;


GalenJsApi = {
    checkLayout: AssertEvents.registerFunction("GalenJsApi.checkLayout"),
    resizeDriver: AssertEvents.registerFunction("GalenJsApi.resizeDriver")
};

describe("GalenJsApi", function () {
    describe("#checkLayout", function () {
        it("should call GalenJsApi.checkLayout", function () {
            AssertEvents.assert(function () {
                _.checkLayout("driver", "page.spec", ["mobile", "desktop"], ["nomobile"]);
            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver", "page.spec", ["mobile", "desktop"], ["nomobile"], null, null, null, [], null]
            }]);
        });

        it("should convert tags single string to array", function () {
            AssertEvents.assert(function () {
                _.checkLayout("driver", "page.spec", "mobile", "nomobile");
            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver", "page.spec", ["mobile"], ["nomobile"], null, null, null, [], null]
            }]);
        });

        it("should call GalenJsApi.checkLayout when calling checkLayout with single argument", function () {
            AssertEvents.assert(function () {
                _.checkLayout({
                    driver: "driver1",
                    spec: "page.spec",
                    tags: ["mobile"],
                    excludedTags: ["nomobile"],
                    sectionFilter: "some section",
                    screenshot: "screenshotFile.png"
                });

            }).shouldBe([{
                name: "GalenJsApi.checkLayout",
                args: ["driver1", "page.spec", ["mobile"], ["nomobile"], "some section", null, "screenshotFile.png", [], null]
            }]);

        });
    });

    describe("#resize", function () {
        it("should call GalenJsApi.resize", function () {
            AssertEvents.assert(function () {
                _.resize({d: "driver"}, "1024x768");
            }).shouldBe([{
                name: "GalenJsApi.resizeDriver",
                args: [{d: "driver"}, "1024x768"]
            }]);
        });
    });

});

