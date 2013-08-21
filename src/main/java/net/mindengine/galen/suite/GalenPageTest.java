package net.mindengine.galen.suite;

import java.awt.Dimension;
import java.util.List;

public class GalenPageTest {
    
    private String url;
    private Dimension screenSize;
    private List<GalenPageAction> actions;
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Dimension getScreenSize() {
        return screenSize;
    }
    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }
    public List<GalenPageAction> getActions() {
        return actions;
    }
    public void setActions(List<GalenPageAction> actions) {
        this.actions = actions;
    }
    public static GalenPageTest readFrom(String text) {
        // TODO read galen page test 
        return null;
    }

}
