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

import java.awt.Rectangle;

public class MaskFilter implements ImageFilter {
    private final ImageHandler maskImage;

    public MaskFilter(ImageHandler maskImage) {
        this.maskImage = maskImage;
    }

    @Override
    public void apply(byte[] bytes, int width, int height, Rectangle area) {
        int maskX, maskY, m, k, averageMaskPixel;

        byte[] maskBytes = maskImage.getBytes();
        int maskWidth = maskImage.getWidth();
        int maskHeight = maskImage.getHeight();


        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;

                maskX = x - area.x;
                maskY = y - area.y;

                if (maskX < maskWidth && maskY < maskHeight) {
                    m = maskY * maskWidth * ImageHandler.BLOCK_SIZE + maskX * ImageHandler.BLOCK_SIZE;

                    averageMaskPixel = (((int) maskBytes[m]) +
                            ((int) maskBytes[m + 1]) +
                            ((int) maskBytes[m + 2])) / 3;
                } else {
                    averageMaskPixel = 255;
                }

                // Changing only alpha
                bytes[k + 3] = (byte) (Math.min(averageMaskPixel, 255) & 0xFF);
            }
        }
    }
}
