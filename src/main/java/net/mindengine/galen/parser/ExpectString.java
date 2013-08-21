package net.mindengine.galen.parser;

import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectString implements Expectation<String>{

    private char quotesSymbol = '"';

    @Override
    public String read(StringCharReader reader) {
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            char symbol = reader.next();
            
            if (symbol == quotesSymbol) {
                break;
            }
            else if (symbol == '\\') {
                if (reader.hasMore()) {
                    buffer.append(reader.next());
                }
                else {
                    buffer.append("\\");
                    break;
                }
            }
            else {
                buffer.append(symbol);
            }
        }
        return buffer.toString();
    }

    public ExpectString setQuotesSymbol(char symbol) {
        this.quotesSymbol = symbol;
        return this;
    }

}
