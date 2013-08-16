package net.mindengine.galen.browser;

import java.awt.Dimension;

import net.mindengine.galen.page.Page;

public interface Browser {

    void quit();

    void changeWindowSize(Dimension screenSize);

    void load(String url);

    void executeJavascript(String javascript);

    Page getPage();

    String getUrl();

    Dimension getScreenSize();

}
