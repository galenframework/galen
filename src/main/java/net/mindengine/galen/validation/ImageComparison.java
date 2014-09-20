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
package net.mindengine.galen.validation;

import net.mindengine.galen.page.Rect;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.awt.image.BufferedImage;

public class ImageComparison {
    private Rect area;
    private String imageSamplePath;
    private BufferedImage comparisonMap;
    private String comparisonMapPath;


    public ImageComparison(Rect area, String imageSamplePath, BufferedImage comparisonMap) {
        this.area = area;
        this.imageSamplePath = imageSamplePath;
        this.comparisonMap = comparisonMap;
    }

    public Rect getArea() {
        return area;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    public String getImageSamplePath() {
        return imageSamplePath;
    }

    public void setImageSamplePath(String imageSamplePath) {
        this.imageSamplePath = imageSamplePath;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ImageComparison))
            return false;

        ImageComparison rhs = (ImageComparison)obj;

        return new EqualsBuilder()
                .append(area, rhs.area)
                .append(imageSamplePath, rhs.imageSamplePath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(area)
                .append(imageSamplePath)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("area", area)
                .append("imageSamplePath", imageSamplePath)
                .toString();
    }

    public String getComparisonMapPath() {
        return comparisonMapPath;
    }


    public BufferedImage getComparisonMap() {
        return comparisonMap;
    }

    public void setComparisonMap(BufferedImage comparisonMap) {
        this.comparisonMap = comparisonMap;
    }

    public void setComparisonMapPath(String comparisonMapPath) {
        this.comparisonMapPath = comparisonMapPath;
    }
}
