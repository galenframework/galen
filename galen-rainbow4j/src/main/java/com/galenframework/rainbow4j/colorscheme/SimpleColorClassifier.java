/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.rainbow4j.colorscheme;

import java.awt.*;

public class SimpleColorClassifier implements ColorClassifier {
    private final int red;
    private final int blue;
    private final int green;
    private String name;

    public SimpleColorClassifier(String name, Color color) {
        this.name = name;
        this.red = color.getRed();
        this.blue = color.getBlue();
        this.green = color.getGreen();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean holdsColor(int r, int g, int b, int maxColorSquareDistance) {
        int distance = (r - red)*(r - red) + (g - green)*(g - green) + (b - blue)*(b - blue);
        return distance < maxColorSquareDistance;
    }
}
