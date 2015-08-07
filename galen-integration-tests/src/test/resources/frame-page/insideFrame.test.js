importClass(com.galenframework.tests.integration.GalenPagesIT);

var FrameLayout = $page("Frame", {
    link: "a.some-link"
});

var MainPage = $page("Main page", {
    iframe: "iframe"
});

var mainPage = new MainPage(driver);

mainPage.iframe.insideFrame(function (frame, driver) {
    var frameLayout = new FrameLayout(driver);

    GalenPagesIT._callbacks.put("insideFrame-test", "text of link inside " + frame.name + ": " + frameLayout.link.getText());
});

