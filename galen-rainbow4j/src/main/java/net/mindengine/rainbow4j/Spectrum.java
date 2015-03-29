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
package net.mindengine.rainbow4j;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class Spectrum {

    private final int[][][] data;
    private int pixelsAmount;
    private int precision;

    public Spectrum(int[][][] data, int width, int height) {
        this.precision = data.length;
        this.data = data;
        this.pixelsAmount = width * height;
    }

    /**
     * 
     * @param red 0 to 255 value of red
     * @param green 0 to 255 value of green
     * @param blue 0 to 255 value of blue
     * @param range 0 to 255 value of range within which it should take histogram value
     * @return
     */
    public float getPercentage(int red, int green, int blue, int range) {

        long counter = 0;
        
        int cr = Math.min(red * precision / 256, precision - 1);
        int cg = Math.min(green * precision / 256, precision - 1);
        int cb = Math.min(blue * precision / 256, precision - 1);
        
        int crange = Math.min(range * precision / 256, precision - 1);
        
        
        int rRange[] = new int[]{Math.max(0, cr - crange), Math.min(cr + crange, precision - 1)};
        int gRange[] = new int[]{Math.max(0, cg - crange), Math.min(cg + crange, precision - 1)};
        int bRange[] = new int[]{Math.max(0, cb - crange), Math.min(cb + crange, precision - 1)};
        
        for (int ir = rRange[0]; ir <= rRange[1]; ir++) {
            for (int ig = gRange[0]; ig <= gRange[1]; ig++) {
                for (int ib = bRange[0]; ib <= bRange[1]; ib++) {
                    counter += data[ir][ig][ib];
                }
            }
        }

        return 100.f * counter/pixelsAmount;
    }
    
    public void printColors() {
        for (int r = 0; r<precision; r++) {
            for (int g = 0; g<precision; g++) {
                for (int b = 0; b<precision; b++) {
                    if (data[r][g][b] > 0) {
                        System.out.println(String.format("(%d, %d, %d) = %d", r, g, b, data[r][g][b]));
                    }
                }
            }
        }
    }

    public int getPrecision() {
        return precision;
    }

    public List<ColorDistribution> getColorDistribution(int minPercentage) {
        double usage = 0;
        
        List<ColorDistribution> colors = new LinkedList<ColorDistribution>(); 
        for (int r = 0; r<precision; r++) {
            for (int g = 0; g<precision; g++) {
                for (int b = 0; b<precision; b++) {
                    usage = data[r][g][b] * 100 / pixelsAmount;
                    
                    if (usage >= minPercentage) {
                        colors.add(new ColorDistribution(new Color(r, g, b), usage));
                    }
                }
            }
        }
        return colors;
    }
}
