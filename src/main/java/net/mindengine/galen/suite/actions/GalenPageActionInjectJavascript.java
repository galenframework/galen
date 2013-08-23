package net.mindengine.galen.suite.actions;

import net.mindengine.galen.suite.GalenPageAction;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionInjectJavascript implements GalenPageAction{

    private String javascriptFilePath;

    public GalenPageActionInjectJavascript(String javascriptFilePath) {
        this.setJavascriptFilePath(javascriptFilePath);
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        
    }

    public String getJavascriptFilePath() {
        return javascriptFilePath;
    }

    public void setJavascriptFilePath(String javascriptFilePath) {
        this.javascriptFilePath = javascriptFilePath;
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
