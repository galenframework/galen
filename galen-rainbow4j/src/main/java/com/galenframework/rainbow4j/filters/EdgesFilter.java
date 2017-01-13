package com.galenframework.rainbow4j.filters;

import com.galenframework.rainbow4j.ImageHandler;

import java.awt.*;
import java.nio.ByteBuffer;

public class EdgesFilter implements ImageFilter {

    private int tolerance = 30;

    public EdgesFilter(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public void apply(ByteBuffer bytes, int width, int height, Rectangle area) {

        int powTolerance = (int) Math.pow(tolerance, 2.0);
        for (int y = area.y; y < area.y + area.height - 1; y++) {
            for (int x = area.x; x < area.x + area.width - 1; x++) {
                int k = y * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;
                int kh = y * width * ImageHandler.BLOCK_SIZE + (x + 1) * ImageHandler.BLOCK_SIZE;
                int kv = (y + 1) * width * ImageHandler.BLOCK_SIZE + x * ImageHandler.BLOCK_SIZE;


                int diffH = 0;
                int diffV = 0;
                for (int i = 0; i < 3; i++) {
                    diffH = (int) (diffH + Math.pow(bytes.get(k + i) - bytes.get(kh + i), 2.0));
                    diffV = (int) (diffH + Math.pow(bytes.get(k + i) - bytes.get(kv + i), 2.0));
                }

                if (diffH > tolerance || diffV > tolerance) {
                    bytes.put(k, (byte) 255);
                    bytes.put(k + 1, (byte) 255);
                    bytes.put(k + 2, (byte) 255);
                } else {
                    bytes.put(k, (byte) 0);
                    bytes.put(k + 1, (byte) 0);
                    bytes.put(k + 2, (byte) 0);
                }
            }
        }
    }

    public int getTolerance() {
        return tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }
}
