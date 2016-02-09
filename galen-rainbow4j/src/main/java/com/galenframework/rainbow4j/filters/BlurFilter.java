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

import com.galenframework.rainbow4j.BufferUtils;
import com.galenframework.rainbow4j.ImageHandler;

import java.awt.*;
import java.nio.ByteBuffer;

/**
 * Created by ishubin on 2014/09/14.
 */
public class BlurFilter implements ImageFilter {
    private int radius;

    public BlurFilter(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void apply(ByteBuffer bytes, int width, int height, Rectangle area) {
        if (area.width + area.x > width || area.height + area.y > height) {
            throw new RuntimeException("Specified area is outside of image");
        }

        if (radius > 0) {
            ByteBuffer copyBytes = BufferUtils.clone(bytes);

            for (int yc = area.y; yc < area.y + area.height; yc++) {
                for (int xc = area.x; xc < area.x + area.width; xc++) {

                    int startY = Math.max(yc - radius, area.y);
                    int startX = Math.max(xc - radius, area.x);
                    int endY = Math.min(yc + radius, area.height + area.y - 1);
                    int endX = Math.min(xc + radius, area.width + area.x - 1);

                    int ar = 0, ag = 0, ab = 0;
                    double sumWeight = 0;
                    double distance;
                    double dWeight;

                    for (int y = startY; y <= endY; y++) {
                        for (int x = startX; x <= endX; x++) {
                            int k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                            int r = copyBytes.get(k) & 0xff;
                            int g = copyBytes.get(k + 1) & 0xff;
                            int b = copyBytes.get(k + 2) & 0xff;

                            distance = Math.max(Math.abs(x - xc), Math.abs(y - yc));
                            dWeight = 1 - distance/(radius + 1);
                            sumWeight += dWeight;

                            ar += r * dWeight;
                            ag += g * dWeight;
                            ab += b * dWeight;
                        }
                    }


                    int k = yc * width * ImageHandler.BLOCK_SIZE + xc * ImageHandler.BLOCK_SIZE;
                    bytes.put(k, (byte) (ar / sumWeight));
                    bytes.put(k + 1, (byte) (ag / sumWeight));
                    bytes.put(k + 2, (byte) (ab / sumWeight));
                }
            }

        }
    }
}
