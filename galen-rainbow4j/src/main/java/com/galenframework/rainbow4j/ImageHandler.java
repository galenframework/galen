/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.rainbow4j;

import com.galenframework.rainbow4j.filters.ImageFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageHandler {
    private ByteBuffer bytes;
    private int width;
    private int height;

    public final static int BLOCK_SIZE = 4;

    public ImageHandler(BufferedImage image) {
        this.bytes = readRgbModelFrom(image);

        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public ImageHandler(int width, int height) {
        this.width = width;
        this.height = height;
        this.bytes = ByteBuffer.allocateDirect(width * height * BLOCK_SIZE);
    }


    private static ByteBuffer readRgbModelFrom(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        int[] pixels = new int[w * h];
        image.getRGB(0, 0, w, h, pixels, 0, w);

        // use direct byte buffer to resolve heap errors here
        ByteBuffer rgbBytes = ByteBuffer.allocateDirect(w * h * BLOCK_SIZE);
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int index = r * w + c;
                int indexRgb = r * w * BLOCK_SIZE + c * BLOCK_SIZE;

                rgbBytes.put(indexRgb, (byte) ((pixels[index] >> 16) & 0xff));
                rgbBytes.put(indexRgb + 1, (byte) ((pixels[index] >> 8) & 0xff));
                rgbBytes.put(indexRgb + 2, (byte) (pixels[index] & 0xff));
                rgbBytes.put(indexRgb + 3, (byte) ((pixels[index] >> 24) & 0xff));
            }
        }

        return rgbBytes;
    }

    public Color pickColor(int x, int y) {
        if (x < width && y < height && x >= 0 && y >= 0) {
            int k = y * width * BLOCK_SIZE + x * BLOCK_SIZE;

            return new Color(bytes.get(k) & 0xff,
                    bytes.get(k + 1) & 0xff,
                    bytes.get(k + 2) & 0xff,
                    bytes.get(k + 3) & 0xff
            );
        } else {
            return new Color(0, 0, 0);
        }
    }

    public static long colorDiff(Color left, Color right) {
        if (left.getAlpha() > 128 && right.getAlpha() > 128) {
            return Math.abs(left.getRed() - right.getRed())
                    + Math.abs(left.getGreen() - right.getGreen())
                    + Math.abs(left.getBlue() - right.getBlue());
        } else {
            return 0L;
        }
    }


    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int index = r * width * BLOCK_SIZE + c * BLOCK_SIZE;
                int red = bytes.get(index) & 0xFF;
                int green = bytes.get(index + 1) & 0xFF;
                int blue = bytes.get(index + 2) & 0xFF;
                int alpha = bytes.get(index + 3) & 0xFF;

                int rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(c, r, rgb);
            }
        }

        return image;
    }

    public void applyFilter(ImageFilter filter, Rectangle area) {
        filter.apply(this.bytes, width, height, area);
    }

    public void setRGBA(int x, int y, int r, int g, int b, int a) {
        int k = y * width * BLOCK_SIZE + x * BLOCK_SIZE;
        bytes.put(k, (byte) (r & 0xff));
        bytes.put(k + 1, (byte) (g & 0xff));
        bytes.put(k + 2, (byte) (b & 0xff));
        bytes.put(k + 3, (byte) (a & 0xff));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getBytes() {
        return bytes;
    }


    public void applyFilter(ImageFilter filter) {
        this.applyFilter(filter, new Rectangle(0, 0, width, height));
    }
}
