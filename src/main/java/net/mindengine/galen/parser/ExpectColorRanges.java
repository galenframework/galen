package net.mindengine.galen.parser;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectColorRanges implements Expectation<List<ColorRange>> {

    @SuppressWarnings("serial")
    private static Map<String, Color> colorWords = new HashMap<String, Color>(){{
       put("black", Color.black);
       put("white", Color.white);
       put("gray", Color.gray);
       put("red", Color.red);
       put("orange", Color.orange);
       put("pink", Color.pink);
       put("green", Color.green);
       put("blue", Color.blue);
       put("yellow", Color.yellow);
       put("magenta", Color.magenta);
       put("cyan", Color.cyan);
    }};
    
    @Override
    public List<ColorRange> read(StringCharReader reader) {
        ExpectRange expectRange = new ExpectRange();
        expectRange.setEndingWord("%");
        
        List<ColorRange> colorRanges = new LinkedList<ColorRange>();
        while(reader.hasMore()) {
            
            Range range = expectRange.read(reader);
            
            String colorText = reader.readUntilSymbol(',').trim();
            
            if (colorText.isEmpty()) {
                throw new SyntaxException("No color defined");
            }
            
            Color color = parseColor(colorText);
            
            colorRanges.add(new ColorRange(color, range));
        }
        return colorRanges;
    }

    private Color parseColor(String colorText) {
        if (colorText.startsWith("#")) {
            return Color.decode(colorText);
        }
        
        if (colorWords.containsKey(colorText)){
            return colorWords.get(colorText);
        }
        else throw new SyntaxException("Unknown color: " + colorText);
    }

}
