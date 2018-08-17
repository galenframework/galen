/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import com.galenframework.rainbow4j.BufferUtils;
import com.galenframework.rainbow4j.ImageHandler;

import java.awt.*;
import java.nio.ByteBuffer;

public class DenoiseFilter implements ImageFilter {
    private int radius;

    public DenoiseFilter(int radius) {
        this.radius = radius;
    }

    @Override
    public void apply(ByteBuffer bytes, int width, int height, Rectangle area) {
        radius = Math.min(radius, Math.min(width / 2, height / 2));

        int normalThreshold = 100;

        if (radius > 0) {

            ByteBuffer copyBytes = BufferUtils.clone(bytes);

            for (int yc = area.y; yc < area.y + area.height; yc++) {
                for (int xc = area.x; xc < area.x + area.width; xc++) {

                    int startY = yc - radius;
                    int startX = xc - radius;
                    int endY = yc + radius;
                    int endX = xc + radius;

                    int ar = 0, ag = 0, ab = 0;
                    double sumWeight = 0;
                    double distance;
                    double dWeight;

                    int r, g, b;

                    for (int y = startY; y <= endY; y++) {
                        for (int x = startX; x <= endX; x++) {

                            if (x >= area.x && x < area.x + area.width
                                   && y >= area.y && y < area.y + area.height) {

                                int k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                                r = copyBytes.get(k) & 0xff;
                                g = copyBytes.get(k + 1) & 0xff;
                                b = copyBytes.get(k + 2) & 0xff;
                            } else {
                                r = 0;
                                g = 0;
                                b = 0;
                            }

                            distance = Math.max(Math.abs(x - xc), Math.abs(y - yc));
                            dWeight = 1 - distance / (radius + 1);
                            sumWeight += dWeight;

                            ar += r * dWeight;
                            ag += g * dWeight;
                            ab += b * dWeight;
                        }
                    }

                    int k = yc * width * ImageHandler.BLOCK_SIZE + xc * ImageHandler.BLOCK_SIZE;

                    if(sumWeight > 0) {

                        int blurredRed = (int) (ar / sumWeight);
                        int blurredGreen = (int) (ag / sumWeight);
                        int blurredBlue = (int) (ab / sumWeight);

                        if (blurredRed < normalThreshold
                            && blurredGreen < normalThreshold
                            && blurredBlue < normalThreshold
                            ) {
                            bytes.put(k, (byte) 0);
                            bytes.put(k + 1, (byte) 0);
                            bytes.put(k + 2, (byte) 0);
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
