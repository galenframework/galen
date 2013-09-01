package net.mindengine.galen.suite.reader;

public class Line {

    public static final Line UNKNOWN_LINE = new Line("", -1);
    
    private String text;
    private int number;
    public Line(String text, int number) {
        this.text = text;
        this.number = number;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public Line trim() {
        return new Line(text.trim(), number);
    }
    public boolean startsWith(String string) {
        return text.startsWith(string);
    }
    
}
