package net.mindengine.galen.specs;

/**
 * Created by ishubin on 2014/11/08.
 */
public class SpecCss extends SpecText {
    private String cssPropertyName;

    public SpecCss(String cssPropertyName, Type type, String text) {
        super(type, text);
        this.cssPropertyName = cssPropertyName;
    }

    public String getCssPropertyName() {
        return cssPropertyName;
    }

    public void setCssPropertyName(String cssPropertyName) {
        this.cssPropertyName = cssPropertyName;
    }
}
