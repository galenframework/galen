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
package net.mindengine.galen.specs;

import net.mindengine.galen.page.Rect;

public class SpecImage extends Spec {
    private String imagePath;
    private Double maxPercentage;
    private Integer maxPixels;
    private Integer tolerance = 25;
    private Integer smooth = 0;
    private Rect selectedArea;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Double getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(Double maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public Integer getMaxPixels() {
        return maxPixels;
    }

    public void setMaxPixels(Integer maxPixels) {
        this.maxPixels = maxPixels;
    }

    public Integer getTolerance() {

        return tolerance;
    }

    public void setTolerance(Integer tolerance) {
        this.tolerance = tolerance;
    }

    public Integer getSmooth() {
        return smooth;
    }

    public void setSmooth(Integer smooth) {
        this.smooth = smooth;
    }

    public Rect getSelectedArea() {
        return selectedArea;
    }

    public void setSelectedArea(Rect selectedArea) {
        this.selectedArea = selectedArea;
    }
}
