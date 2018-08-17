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

                if (diffH > powTolerance || diffV > powTolerance) {
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
