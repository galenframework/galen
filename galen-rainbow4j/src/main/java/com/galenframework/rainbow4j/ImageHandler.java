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
package com.galenframework.rainbow4j;
import com.galenframework.rainbow4j.filters.ImageFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {
    private byte[] bytes;
    private int width;
    private int height;

    private final static int BLOCK_SIZE = 3;

    public ImageHandler(BufferedImage image) {
        this.bytes = readRgbModelFrom(image);

        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public ImageHandler(int width, int height) {
        this.width = width;
        this.height = height;
        this.bytes = new byte[width * height * BLOCK_SIZE];
    }

    public ImageHandler(byte[] bytes, int w, int h) {
        this.bytes = bytes;
        this.width = w;
        this.height = h;
    }


    private static byte[] readRgbModelFrom(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();


        int[] pixels = new int[w * h];
        image.getRGB(0, 0, w, h, pixels, 0, w);

        byte[] rgbBytes = new byte[w * h * BLOCK_SIZE];

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int index = r * w + c;
                int indexRgb = r * w * BLOCK_SIZE + c * BLOCK_SIZE;

                rgbBytes[indexRgb] = (byte)((pixels[index] >> 16) &0xff);
                rgbBytes[indexRgb + 1] = (byte)((pixels[index] >> 8) &0xff);
                rgbBytes[indexRgb + 2] = (byte)(pixels[index] &0xff);
            }
        }

        return rgbBytes;
    }

    public Color getSmoothedColor(int x, int y, int x2, int y2) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x >= width || y >= height) {
            throw new RuntimeException("pixel is out of range");
        }

        if (x2 >= width) x2 = width - 1;
        if (y2 >= height) y2 = height - 1;

        int i, j;

        long r = 0, g = 0, b = 0, t = 0;

        for (i = x; i <= x2; i++) {
            for (j = y; j <= y2; j++) {
                t += 1;
                Color c = pickColor(i, j);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }

        if (t > 0) {
            return new Color((int)(r/t), (int)(g/t), (int)(b/t));
        }
        else return new Color(0, 0, 0);
    }

    public Color pickColor(int x, int y) {
        if (x < width && y < height && x >= 0 && y >= 0) {
            int k = y * width * BLOCK_SIZE + x * BLOCK_SIZE;

            return new Color(bytes[k] & 0xff,
                    bytes[k + 1] & 0xff,
                    bytes[k + 2] & 0xff
            );
        }
        else {
            return new Color(0, 0, 0);
        }
    }

    public static long colorDiff(Color cA, Color cB) {
        return Math.abs(cA.getRed() - cB.getRed())
               + Math.abs(cA.getGreen() - cB.getGreen())
               + Math.abs(cB.getBlue() - cB.getBlue());
    }


    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int r=0; r< height; r++) {
            for (int c = 0; c < width; c++) {
                int index = r * width * BLOCK_SIZE + c * BLOCK_SIZE;
                int red = bytes[index] & 0xFF;
                int green = bytes[index + 1] & 0xFF;
                int blue = bytes[index + 2] & 0xFF;
                int rgb = (red << 16) | (green << 8) | blue;
                image.setRGB(c, r, rgb);
            }
        }

        return image;
    }

    public void applyFilter(ImageFilter filter, Rectangle area) {
        filter.apply(this.bytes, width, height, area);
    }

    public void setRGB(int x, int y, int r, int g, int b) {
        int k = y * width * BLOCK_SIZE + x * BLOCK_SIZE;
        bytes[k] = (byte) (r & 0xff);
        bytes[k + 1] = (byte) (g & 0xff);
        bytes[k + 2] = (byte) (b & 0xff);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getBytes() {
        return bytes;
    }


    public void applyFilter(ImageFilter filter) {
        this.applyFilter(filter, new Rectangle(0, 0, width, height));
    }
}
