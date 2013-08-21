package net.mindengine.galen.suite.actions;

import net.mindengine.galen.suite.GalenPageAction;

public class GalenPageActionInjectJavascript implements GalenPageAction{

    private Object javascriptFilePath;

    public GalenPageActionInjectJavascript(String javascriptFilePath) {
        this.setJavascriptFilePath(javascriptFilePath);
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        
    }

    public Object getJavascriptFilePath() {
        return javascriptFilePath;
    }

    public void setJavascriptFilePath(Object javascriptFilePath) {
        this.javascriptFilePath = javascriptFilePath;
    }

}
