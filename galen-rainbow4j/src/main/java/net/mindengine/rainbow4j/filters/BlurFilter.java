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

import net.mindengine.rainbow4j.ImageHandler;
import net.mindengine.rainbow4j.filters.ImageFilter;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;

/**
 * Created by ishubin on 2014/09/14.
 */
public class BlurFilter implements ImageFilter {
    private static final int BLOCK_SIZE = 3;
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
    public void apply(byte[] bytes, int width, int height, Rectangle area) {
        if (area.width + area.x > width || area.height + area.y > height) {
            throw new RuntimeException("Specified area is outside of image");
        }

        if (radius > 1) {
            byte[] copyBytes = ArrayUtils.clone(bytes);

            for (int yc = area.y; yc < area.height; yc++) {
                for (int xc = area.x; xc < area.width; xc++) {

                    int startY = Math.max(yc - radius, 0);
                    int startX = Math.max(xc - radius, 0);
                    int endY = Math.min(yc + radius, height);
                    int endX = Math.min(xc + radius, width);

                    int ar = 0, ag = 0, ab = 0;
                    double sumWeight = 0;
                    double distance;
                    double dWeight;

                    for (int y = startY; y < endY; y++) {
                        for (int x = startX; x < endX; x++) {
                            int k = y * width * BLOCK_SIZE + x * BLOCK_SIZE;
                            int r = copyBytes[k] & 0xff;
                            int g = copyBytes[k + 1] & 0xff;
                            int b = copyBytes[k + 2] & 0xff;

                            distance = Math.max(Math.abs(x - xc), Math.abs(y - yc));
                            dWeight = 1 - distance/(radius + 1);
                            sumWeight += dWeight;

                            ar += r * dWeight;
                            ag += g * dWeight;
                            ab += b * dWeight;
                        }
                    }


                    int k = yc * width * BLOCK_SIZE + xc * BLOCK_SIZE;
                    bytes[k] = (byte) (ar / sumWeight);
                    bytes[k + 1] = (byte) (ag / sumWeight);
                    bytes[k + 2] = (byte) (ab / sumWeight);
                }
            }

        }
    }
}
