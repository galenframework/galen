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
                    buffer.append(asEscapeSymbol(reader.next()));
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

    private char asEscapeSymbol(char symbol) {
        if (symbol == 'n') {
            return '\n';
        }
        if (symbol == 't') {
            return '\t';
        }
        if (symbol == 'b') {
            return '\b';
        }
        if (symbol == 'r') {
            return '\r';
        }
        if (symbol == 'f') {
            return '\f';
        }
        else return symbol;
    }

    public ExpectString setQuotesSymbol(char symbol) {
        this.quotesSymbol = symbol;
        return this;
    }

}
