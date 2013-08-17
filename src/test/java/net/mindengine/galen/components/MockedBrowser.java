package net.mindengine.galen.components;

import java.awt.Dimension;
import java.util.UUID;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;

public class MockedBrowser implements Browser {

    private String url;
    private Dimension screenSize;

    public MockedBrowser(String url, Dimension screenSize) {
        this.url = url;
        this.screenSize = screenSize;
    }

    @Override
    public void quit() {
    }

    @Override
    public void changeWindowSize(Dimension screenSize) {
    }

    @Override
    public void load(String url) {
    }

    @Override
    public void executeJavascript(String javascript) {
    }

    @Override
    public Page getPage() {
        return null;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public Dimension getScreenSize() {
        return this.screenSize;
    }

    @Override
    public String createScreenshot() {
        return "/tmp/screenshot-" + UUID.randomUUID().toString() + ".png";
    }

}
