package net.mindengine.galen.suite.actions;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionInjectJavascript implements GalenPageAction{

    private static final List<ValidationError> NO_ERRORS = new LinkedList<ValidationError>();
    
    private String javascriptFilePath;

    public GalenPageActionInjectJavascript(String javascriptFilePath) {
        this.setJavascriptFilePath(javascriptFilePath);
    }
    public String getJavascriptFilePath() {
        return javascriptFilePath;
    }

    public void setJavascriptFilePath(String javascriptFilePath) {
        this.javascriptFilePath = javascriptFilePath;
    }
    
    @Override
    public List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) {
        browser.executeJavascript(getJavascriptFilePath());
        return NO_ERRORS;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(javascriptFilePath)
            .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionInjectJavascript))
            return false;
        
        GalenPageActionInjectJavascript rhs = (GalenPageActionInjectJavascript)obj;
        
        return new EqualsBuilder()
            .append(javascriptFilePath, rhs.javascriptFilePath)
            .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("javascriptFilePath", javascriptFilePath)
            .toString();
    }
}
