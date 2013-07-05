package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.reader.Expectations.isWordDelimeter;


public class ExpectWord implements Expectation<String> {

    private char breakSymbol = 0;

    @Override
    public String read(StringCharReader reader) {
        boolean started = false;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            
            if (breakSymbol != 0 && !started && symbol == breakSymbol) {
                reader.back();
                return "";
            }
            
            if(isWordDelimeter(symbol)) {
                if (started) {
                    reader.back();
                    break;
                }
            }
            else {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }

    public ExpectWord stopOnThisSymbol(char breakSymbol) {
        this.breakSymbol = breakSymbol;
        return this;
    }
    
}
