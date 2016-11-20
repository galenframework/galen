load("galen-bootstrap/galen-bootstrap.js");
this._fullProjectPath = System.getProperty("sample.project.path");

var TEST_USER = {
    username: "testuser@example.com",
    password: "test123"
};

$galen.settings.website = System.getProperty("sample.test.website");

$galen.registerDevice("mobile", inSingleBrowser("mobile emulation", "450x700", ["mobile"]));
$galen.registerDevice("tablet", inSingleBrowser("tablet emulation", "600x700", ["tablet"]));
$galen.registerDevice("desktop", inSingleBrowser("desktop emulation", "1024x768", ["desktop"]));


function customCheckLayout(driver, specPath, tags) {
    checkLayout(driver, _fullProjectPath + "/" + specPath, tags);
}

(function (export) {
    export.TEST_USER = TEST_USER;
    export.customCheckLayout = customCheckLayout;
})(this);
