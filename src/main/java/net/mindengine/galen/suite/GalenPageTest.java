package net.mindengine.galen.suite;

import java.awt.Dimension;
import java.util.List;

import net.mindengine.galen.browser.BrowserFactory;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageTest {
    
    private String url;
    private Dimension screenSize;
    private List<GalenPageAction> actions;
    private BrowserFactory browserFactory;
    
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
    
    public GalenPageTest withSize(Dimension size) {
        setScreenSize(size);
        return this;
    }
    public GalenPageTest withUrl(String url) {
        this.url = url;
        return this;
    }
    public GalenPageTest withSize(int width, int height) {
        this.screenSize = new Dimension(width, height);
        return this;
    }
    public GalenPageTest withBrowserFactory(BrowserFactory browserFactory) {
        this.setBrowserFactory(browserFactory);
        return this;
    }
    public BrowserFactory getBrowserFactory() {
        return browserFactory;
    }
    public void setBrowserFactory(BrowserFactory browserFactory) {
        this.browserFactory = browserFactory;
    }
    
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.actions)
            .append(this.browserFactory)
            .append(this.screenSize)
            .append(this.url)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("url", this.url)
            .append("screenSize", this.screenSize)
            .append("browserFactory", this.browserFactory)
            .append("actions", this.actions)
            .toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GalenPageTest)) {
            return false;
        }
        GalenPageTest rhs = (GalenPageTest)obj;
        
        return new EqualsBuilder()
            .append(this.actions, rhs.actions)
            .append(this.browserFactory, rhs.browserFactory)
            .append(this.screenSize, rhs.screenSize)
            .append(this.url, rhs.url)
            .isEquals();
    }
    
}
