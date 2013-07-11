package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.specs.reader.IncorrectSpecException;

public class PageSpecReaderException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1698658011707718651L;

    private String specFile;
    private int specLine;
    
    
    public PageSpecReaderException(Exception cause, String specFile, int specLine) {
        super(cause);
        this.specFile = specFile;
        this.specLine = specLine;
    }
    public String getSpecFile() {
        return specFile;
    }
    public int getSpecLine() {
        return specLine;
    }
    
    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (cause instanceof IncorrectSpecException) {
            return cause.getMessage();
        }
        return super.getMessage();
    }
}
