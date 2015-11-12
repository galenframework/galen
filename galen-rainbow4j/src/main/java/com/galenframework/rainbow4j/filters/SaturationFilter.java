/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import java.awt.*;

public class SaturationFilter implements ImageFilter {
    private int level;

    public SaturationFilter(int level) {
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

        if (level > 100) {
            level = 100;
        }
        else if (level < 0) {
            level = 0;
        }

        double t = level / 100.0;
        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                int k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                double red = bytes[k] & 0xff;
                double green = bytes[k + 1] & 0xff;
                double blue = bytes[k + 2] & 0xff;

                double gray = green * 0.59 + red * 0.3 + blue * 0.11;
                bytes[k] = (byte) colorRange(gray * (1.0 - t) + red * t);
                bytes[k + 1] = (byte) colorRange(gray * (1.0 - t) + green * t);
                bytes[k + 2] = (byte) colorRange(gray * (1.0 - t) + blue * t);
            }
        }
    }

    private int colorRange(double color) {
        int c = (int) color;
        if (c > 255) {
            return 255;
        }
        else if (c < 0) {
            return 0;
        }
        else return c;

    }
}
