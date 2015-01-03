/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.rainbow4j.filters.ImageFilter;

import java.util.LinkedList;
import java.util.List;

import static net.mindengine.galen.parser.Expectations.number;

public class SpecImage extends Spec {

    public enum ErrorRateType {
        PIXELS("px"), PERCENT("%");
        private final String name;

        private ErrorRateType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ErrorRate {
        private Double value;
        private ErrorRateType type;
        public ErrorRate(Double value, ErrorRateType type) {
            this.value = value;
            this.type = type;
        }
        public Double getValue() {
            return value;
        }
        public void setValue(Double value) {
            this.value = value;
        }
        public SpecImage.ErrorRateType getType() {
            return type;
        }
        public void setType(ErrorRateType type) {
            this.type = type;
        }

        public static ErrorRate fromString(String errorRateText) {
            if (errorRateText == null || errorRateText.trim().isEmpty()) {
                return new ErrorRate(0.0, ErrorRateType.PIXELS);
            }
            StringCharReader reader = new StringCharReader(errorRateText);
            Double value = number().read(reader);
            String rest = reader.getTheRest().trim();

            ErrorRateType type;
            if (rest.isEmpty() || rest.equals("px")) {
                type = ErrorRateType.PIXELS;
            }
            else if (rest.equals("%")) {
                type = ErrorRateType.PERCENT;
            }
            else {
                throw new SyntaxException("Can't read error rate value for image spec: " + errorRateText);
            }

            return new ErrorRate(value, type);
        }
    }

    private List<String> imagePaths;
    private ErrorRate errorRate;

    private Integer tolerance;
    private List<ImageFilter> originalFilters = new LinkedList<ImageFilter>();
    private List<ImageFilter> sampleFilters = new LinkedList<ImageFilter>();
    private List<ImageFilter> mapFilters = new LinkedList<ImageFilter>();
    private Rect selectedArea;
    private boolean stretch = false;
    private boolean cropIfOutside = false;

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

    public boolean isCropIfOutside() {
        return cropIfOutside;
    }

    public void setCropIfOutside(boolean cropIfOutside) {
        this.cropIfOutside = cropIfOutside;
    }

    public SpecImage.ErrorRate getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(ErrorRate errorRate) {
        this.errorRate = errorRate;
    }

}
