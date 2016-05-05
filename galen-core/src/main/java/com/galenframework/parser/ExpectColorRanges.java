/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.parser;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.galenframework.rainbow4j.colorscheme.ColorClassifier;
import com.galenframework.rainbow4j.colorscheme.GradientColorClassifier;
import com.galenframework.rainbow4j.colorscheme.SimpleColorClassifier;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.specs.Range;

import static java.util.Arrays.asList;

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
        
        List<ColorRange> colorRanges = new LinkedList<>();
        while(reader.hasMore()) {
            
            Range range = expectRange.read(reader);

            String colorText = reader.readSafeUntilSymbol(',').trim();

            if (colorText.isEmpty()) {
                throw new SyntaxException("No color defined");
            }


            ColorClassifier colorClassifier = parseColorClassifier(colorText);
            colorRanges.add(new ColorRange(colorText, colorClassifier, range));
        }
        return colorRanges;
    }

    public static ColorClassifier parseColorClassifier(String colorText) {
        if (colorText.contains("-")) {
            return parseGradientClassifier(colorText);
        } else {
            Color color = parseColor(colorText);
            return new SimpleColorClassifier(colorText, color);
        }
    }

    public static GradientColorClassifier parseGradientClassifier(String colorText) {
        //parsing gradients
        List<Color> colors = asList(colorText.split("-")).stream()
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .map(ExpectColorRanges::parseColor)
                .collect(Collectors.toList());

        return new GradientColorClassifier(colorText, colors);
    }

    public static Color parseColor(String colorText) {
        if (colorText.startsWith("#")) {
            if (colorText.length() == 4) {
                return Color.decode(convertShortHandNotation(colorText));
            } else {
                return Color.decode(colorText);
            }
        }
        
        if (colorWords.containsKey(colorText)){
            return colorWords.get(colorText);
        }
        else throw new SyntaxException("Unknown color: " + colorText);
    }

    private static String convertShortHandNotation(String colorText) {
        char r = colorText.charAt(1);
        char g = colorText.charAt(2);
        char b = colorText.charAt(3);

        return new StringBuilder("#")
                .append(r).append(r)
                .append(g).append(g)
                .append(b).append(b)
                .toString();
    }

}
