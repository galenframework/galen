importClass(com.galenframework.components.mocks.driver.MockedDriver);

test("Simple test", function () {
    var driver = new MockedDriver("/suites/custom-js-variables-for-checklayout/page-mock.json");
    checkLayout({
        driver: driver,
        spec: "/suites/custom-js-variables-for-checklayout/page.gspec",
        vars: {
            customData: {
                userName: "Jack"
            }
        }
    });
});


