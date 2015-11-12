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
