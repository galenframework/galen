package net.mindengine.galen.validation;

public class ValidationErrorException extends Exception {

    public ValidationErrorException() {
        super();
    }

    public ValidationErrorException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public ValidationErrorException(String paramString) {
        super(paramString);
    }

    public ValidationErrorException(Throwable paramThrowable) {
        super(paramThrowable);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -1566513657187992205L;

}
