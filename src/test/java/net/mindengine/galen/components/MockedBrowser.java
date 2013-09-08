package net.mindengine.galen.components;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.google.common.io.Files;

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
        File tempDir = Files.createTempDir();
        
        File file = new File(tempDir.getAbsolutePath() + UUID.randomUUID().toString() + ".png");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}
