package net.mindengine.galen.specs.reader;

public class StringCharReader {

    private String text;
    private int length;
    private int cursor = 0;
    

    public StringCharReader(String text) {
        this.text = text;
        this.length = text.length();
    }
    
    public void back() {
        cursor--;
        if (cursor < 0) {
            cursor = 0;
        }
    }

    public boolean hasMore() {
        return cursor < length;
    }

    public char next() {
        if(cursor == length) {
            throw new IndexOutOfBoundsException();
        }
        char symbol = text.charAt(cursor);
        cursor++;
        return symbol;
    }

}
