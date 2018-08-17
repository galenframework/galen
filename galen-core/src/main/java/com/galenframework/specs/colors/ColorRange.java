/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.specs.colors;

import java.awt.Color;

import com.galenframework.rainbow4j.colorscheme.ColorClassifier;
import com.galenframework.specs.Range;

public class ColorRange {

    private Range range;
    private ColorClassifier colorClassifier;
    private String name;

    public ColorRange(String name, ColorClassifier colorClassifier, Range range) {
        this.name = name;
        this.colorClassifier = colorClassifier;
        this.range = range;
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColorClassifier getColorClassifier() {
        return colorClassifier;
    }

    public void setColorClassifier(ColorClassifier colorClassifier) {
        this.colorClassifier = colorClassifier;
    }
}
