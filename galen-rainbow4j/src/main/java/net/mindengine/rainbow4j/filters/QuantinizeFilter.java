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

public class QuantinizeFilter implements ImageFilter {
    private int colorsAmount;

    public QuantinizeFilter(int colorsAmount) {
        this.colorsAmount = colorsAmount;
    }

    public int getColorsAmount() {
        return colorsAmount;
    }

    public void setColorsAmount(int colorsAmount) {
        this.colorsAmount = colorsAmount;
    }

    @Override
    public void apply(byte[] bytes, int width, int height, Rectangle area) {

        if (colorsAmount > 255) {
            colorsAmount = 255;
        }
        else if (colorsAmount < 2) {
           colorsAmount = 2;
        }


        int d = 256 / colorsAmount;

        for (int y = area.y; y < area.height; y++) {
            for (int x = area.x; x < area.width; x++) {
                int k = y * width * 3 + x * 3;
                double red = (bytes[k] & 0xff) / d;
                double green = (bytes[k + 1] & 0xff) / d;
                double blue = (bytes[k + 2] & 0xff) / d;


                bytes[k] = (byte) (Math.ceil(red) * d);
                bytes[k + 1] = (byte) (Math.ceil(green) * d);
                bytes[k + 2] = (byte) (Math.ceil(blue) * d);
            }
        }
    }
}
