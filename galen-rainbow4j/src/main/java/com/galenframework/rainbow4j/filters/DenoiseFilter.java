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

public class DenoiseFilter implements ImageFilter {
    private int radius;

    public DenoiseFilter(int radius) {
        this.radius = radius;
    }

    @Override
    public void apply(byte[] bytes, int width, int height, Rectangle area) {
        radius = Math.min(radius, Math.min(width / 2, height / 2));

        int maximumPossiblePixels = (radius * 2 + 1) * (radius * 2 + 1);

        if (radius > 0) {
            for (int yc = 0; yc < height; yc++) {
                for (int xc = 0; xc < width; xc++) {

                    int blackPixels = 0;
                    int whitePixels = 0;
                    int total = 0;

                    int startY = Math.max(yc - radius, 0);
                    int startX = Math.max(xc - radius, 0);
                    int endY = Math.min(yc + radius, height - 1);
                    int endX = Math.min(xc + radius, width - 1);

                    for (int y = startY; y <= endY; y++) {
                        for (int x = startX; x <= endX; x++) {
                            int k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                            int r = bytes[k] & 0xff;
                            int g = bytes[k + 1] & 0xff;
                            int b = bytes[k + 2] & 0xff;

                            if (r < 10 && g < 10 && b < 10) {
                                blackPixels ++;
                            }
                            else {
                                whitePixels ++;
                            }

                            total++;
                        }
                    }

                    double amountRatio = ((double) total) / ((double) maximumPossiblePixels);

                    int k = yc * width * ImageHandler.BLOCK_SIZE + xc * ImageHandler.BLOCK_SIZE;
                    if (whitePixels > 0) {
                        if ((amountRatio > 0.6 && blackPixels / whitePixels > 3) // matching normal pixels
                            || (amountRatio <= 0.6 && blackPixels / whitePixels >= 2) // matching pixels that are on border
                                ) {
                            bytes[k] = 0;
                            bytes[k + 1] = 0;
                            bytes[k + 2] = 0;
                        }
                    }
                }
            }
        }
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
