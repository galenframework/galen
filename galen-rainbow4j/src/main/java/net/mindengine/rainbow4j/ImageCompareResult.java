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

import java.awt.image.BufferedImage;

public class ImageCompareResult {

    private double percentage;
    private long totalPixels;
    private BufferedImage comparisonMap;
    private BufferedImage originalFilteredImage;
    private BufferedImage sampleFilteredImage;

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setTotalPixels(long totalPixels) {
        this.totalPixels = totalPixels;
    }

    public long getTotalPixels() {
        return totalPixels;
    }

    public BufferedImage getComparisonMap() {
        return comparisonMap;
    }

    public void setComparisonMap(BufferedImage comparisonMap) {
        this.comparisonMap = comparisonMap;
    }

    public void setOriginalFilteredImage(BufferedImage originalFilteredImage) {
        this.originalFilteredImage = originalFilteredImage;
    }

    public BufferedImage getOriginalFilteredImage() {
        return originalFilteredImage;
    }

    public void setSampleFilteredImage(BufferedImage sampleFilteredImage) {
        this.sampleFilteredImage = sampleFilteredImage;
    }

    public BufferedImage getSampleFilteredImage() {
        return sampleFilteredImage;
    }
}
