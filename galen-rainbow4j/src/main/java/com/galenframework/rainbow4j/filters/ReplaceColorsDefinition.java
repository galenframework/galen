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

import com.galenframework.rainbow4j.colorscheme.ColorClassifier;

import java.awt.*;
import java.util.List;

public class ReplaceColorsDefinition {
    public static final int DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM = 50;
    public static final int DEFAULT_RADIUS = 1;

    private Color replaceColor;
    private List<ColorClassifier> colorClassifiers;
    private int tolerance =  DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM;
    private int radius = DEFAULT_RADIUS;

    public ReplaceColorsDefinition(Color replaceColor, List<ColorClassifier> colorClassifiers) {
        this.replaceColor = replaceColor;
        this.colorClassifiers = colorClassifiers;
    }

    public Color getReplaceColor() {
        return replaceColor;
    }

    public void setReplaceColor(Color replaceColor) {
        this.replaceColor = replaceColor;
    }

    public List<ColorClassifier> getColorClassifiers() {
        return colorClassifiers;
    }

    public void setColorClassifiers(List<ColorClassifier> colorClassifiers) {
        this.colorClassifiers = colorClassifiers;
    }

    public int getTolerance() {
        return tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
