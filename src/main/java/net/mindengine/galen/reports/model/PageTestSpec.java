package net.mindengine.galen.reports.model;

import java.util.List;

import net.mindengine.galen.validation.ErrorArea;

public class PageTestSpec {
    
    private String text;
    private Boolean failed = false;
    private String screenshot;
    private List<String> errorMessages;
    private List<ErrorArea> errorAreas;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    public void setErrorAreas(List<ErrorArea> errorAreas) {
        this.errorAreas = errorAreas;
    }
    public List<ErrorArea> getErrorAreas() {
        return this.errorAreas;
    }
}
