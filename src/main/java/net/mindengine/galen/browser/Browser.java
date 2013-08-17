package net.mindengine.galen.browser;

import java.awt.Dimension;

import net.mindengine.galen.page.Page;

public interface Browser {

    void quit();

    void changeWindowSize(Dimension screenSize);

    void load(String url);

    void executeJavascript(String javascript);

    Page getPage();

    /**
     * Returns the current page url
     * @return Current page url
     */
    String getUrl();

    Dimension getScreenSize();

    /**
     * Makes a screenshot in a temporary folder
     * @return Path to screenshot file
     */
    String createScreenshot();

}
