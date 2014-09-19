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
import net.mindengine.rainbow4j.filters.ImageFilter;

import java.util.LinkedList;
import java.util.List;

public class SpecImage extends Spec {
    private List<String> imagePaths;
    private Double maxPercentage;
    private Integer maxPixels;
    private Integer tolerance = 25;
    private List<ImageFilter> originalFilters = new LinkedList<ImageFilter>();
    private List<ImageFilter> sampleFilters = new LinkedList<ImageFilter>();
    private List<ImageFilter> mapFilters = new LinkedList<ImageFilter>();
    private Rect selectedArea;
    private boolean stretch;


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

    public Rect getSelectedArea() {
        return selectedArea;
    }

    public void setSelectedArea(Rect selectedArea) {
        this.selectedArea = selectedArea;
    }

    public List<ImageFilter> getMapFilters() {
        return mapFilters;
    }

    public void setMapFilters(List<ImageFilter> mapFilters) {
        this.mapFilters = mapFilters;
    }

    public boolean isStretch() {
        return stretch;
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public List<ImageFilter> getOriginalFilters() {
        return originalFilters;
    }

    public void setOriginalFilters(List<ImageFilter> originalFilters) {
        this.originalFilters = originalFilters;
    }

    public List<ImageFilter> getSampleFilters() {
        return sampleFilters;
    }

    public void setSampleFilters(List<ImageFilter> sampleFilters) {
        this.sampleFilters = sampleFilters;
    }
}
