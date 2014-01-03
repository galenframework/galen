/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
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
