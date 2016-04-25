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
package com.galenframework.rainbow4j.filters;

import com.galenframework.rainbow4j.ImageHandler;
import com.galenframework.rainbow4j.colorscheme.ColorClassifier;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Predicate;

public class ReplaceColorsFilter implements ImageFilter {
    public static final int DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM = 3;
    private List<ReplaceColorsDefinition> replaceColorsDefinitions;

    public ReplaceColorsFilter(List<ReplaceColorsDefinition> replaceColorsDefinitions) {
        this.replaceColorsDefinitions = replaceColorsDefinitions;
    }

    @Override
    public void apply(ByteBuffer bytes, int width, int height, Rectangle area) {
        int k, r, g, b;
        int maxColorDistance = DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM * DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM * 3;

        if (replaceColorsDefinitions != null && !replaceColorsDefinitions.isEmpty()) {
            for (int y = area.y; y < area.y + area.height; y++) {
                for (int x = area.x; x < area.x + area.width; x++) {
                    k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                    r = bytes.get(k) & 0xff;
                    g = bytes.get(k + 1) & 0xff;
                    b = bytes.get(k + 2) & 0xff;

                    for (ReplaceColorsDefinition colorDefinition : replaceColorsDefinitions) {
                        if (colorDefinition.getColorClassifiers() != null) {
                            if (colorDefinition.getColorClassifiers().stream().filter(byHoldingColor(r, g, b, maxColorDistance)).findAny().isPresent()) {
                                //replace color
                                Color replaceColor = colorDefinition.getReplaceColor();
                                bytes.put(k, (byte) replaceColor.getRed());
                                bytes.put(k + 1, (byte) replaceColor.getGreen());
                                bytes.put(k + 2, (byte) replaceColor.getBlue());
                            }
                        }
                    }
                }
            }
        }
    }

    private Predicate<ColorClassifier> byHoldingColor(int r, int g, int b, int maxColorDistance) {
        return c -> c.holdsColor(r, g, b, maxColorDistance);
    }

    public List<ReplaceColorsDefinition> getReplaceColorsDefinitions() {
        return replaceColorsDefinitions;
    }

    public void setReplaceColorsDefinitions(List<ReplaceColorsDefinition> replaceColorsDefinitions) {
        this.replaceColorsDefinitions = replaceColorsDefinitions;
    }
}
