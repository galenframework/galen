package net.mindengine.galen.suite.actions;

import net.mindengine.galen.suite.GalenPageAction;

public class GalenPageActionExecuteJavascript implements GalenPageAction{

    private String javascriptPath;
    private String jsonArguments;

    public GalenPageActionExecuteJavascript(String javascriptPath) {
        this.setJavascriptPath(javascriptPath);
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        
    }

    public String getJavascriptPath() {
        return javascriptPath;
    }

    public void setJavascriptPath(String javascriptPath) {
        this.javascriptPath = javascriptPath;
    }

    public GalenPageAction withArguments(String jsonArguments) {
        this.setJsonArguments(jsonArguments);
        return this;
    }

    public String getJsonArguments() {
        return jsonArguments;
    }

    public void setJsonArguments(String jsonArguments) {
        this.jsonArguments = jsonArguments;
    }

}
