package net.mindengine.galen.specs.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Side;

public class ExpectSides implements Expectation<List<Side>>{

    @Override
    public List<Side> read(StringCharReader reader) {
        ExpectWord expectWord = new ExpectWord();
        
        List<Side> sides = new LinkedList<Side>();
        
        while(reader.hasMore()) {
            String side = expectWord.stopOnThisSymbol(',').read(reader);
            if (!side.isEmpty()) {
                sides.add(Side.fromString(side));
            }
            if (reader.currentSymbol() == ',') {
                break;
            }
        }
        if (sides.size() == 0) {
            throw new IncorrectSpecException("There are no sides defined for location");
        }
        return sides;
    }
    
    
}
