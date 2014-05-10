package net.mindengine.galen.reports;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionReportNode extends TestReportNode {

    private Throwable exception;

    public ExceptionReportNode(Throwable exception) {
        this.exception = exception;
    }
    
    @Override
    public String getName() {
        return ExceptionUtils.getMessage(exception);
    }
    
    @Override
    public Status getStatus() {
        return TestReportNode.Status.ERROR;
    }
    
    public String getStacktrace() {
        return ExceptionUtils.getStackTrace(exception);
    }

}
