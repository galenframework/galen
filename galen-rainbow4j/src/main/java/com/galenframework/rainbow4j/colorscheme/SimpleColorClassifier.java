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
package com.galenframework.rainbow4j.colorscheme;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(red)
                .append(blue)
                .append(green)
                .append(name)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof SimpleColorClassifier))
            return false;

        SimpleColorClassifier rhs = (SimpleColorClassifier)obj;
        return new EqualsBuilder()
                .append(rhs.red, this.red)
                .append(rhs.blue, this.blue)
                .append(rhs.green, this.green)
                .append(rhs.name, this.name)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("red", this.red)
                .append("blue", this.blue)
                .append("green", this.green)
                .append("name", this.name)
                .toString();
    }
}
