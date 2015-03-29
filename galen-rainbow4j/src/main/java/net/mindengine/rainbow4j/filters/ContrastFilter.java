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
package net.mindengine.rainbow4j.filters;

import java.awt.*;

public class ContrastFilter implements  ImageFilter{
    private int level;

    public ContrastFilter(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void apply(byte[] bytes, int width, int height, Rectangle area) {
        if (level > 259) {
            level = 258;
        }
        int factor = 259*(level + 255) / (255*(259 - level));

        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                int k = y * width * 3 + x * 3;

                bytes[k] = contrast(bytes[k], factor);
                bytes[k + 1] = contrast(bytes[k + 1], factor);
                bytes[k + 2] = contrast(bytes[k + 2], factor);
            }
        }
    }

    private byte contrast(byte color, int factor) {
        int colorInt = (int)color & 0xFF;
        colorInt = (colorInt - 128) * factor + 128;

        if (colorInt < 0) {
            colorInt = 0;
        }
        else if (colorInt > 255) {
            colorInt = 255;
        }
        return (byte)colorInt;
    }
}
