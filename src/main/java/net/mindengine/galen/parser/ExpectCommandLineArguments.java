package net.mindengine.galen.parser;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectCommandLineArguments implements Expectation<String[]> {

    @Override
    public String[] read(StringCharReader reader) {
        
        ExpectWord expectWord = new ExpectWord().withDelimeters(' ').stopOnTheseSymbols('"', '\'');
        ExpectString expectString = new ExpectString();
        
        
        List<String> arguments = new LinkedList<String>();
        
        while(reader.hasMore()) {
            String word = expectWord.read(reader);
            
            if (!word.isEmpty()) {
                arguments.add(word);
            }
            
            
            if (reader.currentSymbol() == '"' || reader.currentSymbol() == '\'') {
                expectString.setQuotesSymbol(reader.currentSymbol());
                if (reader.hasMore()) {
                    reader.next();
                    String string = expectString.read(reader);
                    arguments.add(string);
                }
            }
        }
        
        
        return arguments.toArray(new String[]{});
    }

}
