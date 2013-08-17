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
        // TODO Auto-generated method stub

    }

    @Override
    public void changeWindowSize(Dimension screenSize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void load(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void executeJavascript(String javascript) {
        // TODO Auto-generated method stub

    }

    @Override
    public Page getPage() {
        // TODO Auto-generated method stub
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
