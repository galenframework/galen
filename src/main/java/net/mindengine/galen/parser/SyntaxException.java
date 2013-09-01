package net.mindengine.galen.parser;

import net.mindengine.galen.suite.reader.Line;

public class SyntaxException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 421348434010710101L;

    private Line line;
    
    public SyntaxException(Line line) {
        super();
        this.line = line; 
    }

    public SyntaxException(Line line, String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        this.line = line;
    }

    public SyntaxException(Line line, String paramString) {
        super(paramString);
        this.line = line;
    }

    public SyntaxException(Line line, Throwable paramThrowable) {
        super(paramThrowable);
        this.line = line;
    }
    
    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
