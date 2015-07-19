/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation;

import java.awt.image.BufferedImage;

public class ImageComparison {
    private BufferedImage sampleFilteredImage;
    private BufferedImage originalFilteredImage;
    private BufferedImage comparisonMap;


    public ImageComparison(BufferedImage originalFilteredImage,
                           BufferedImage sampleFilteredImage, BufferedImage comparisonMap) {
        this.originalFilteredImage = originalFilteredImage;
        this.sampleFilteredImage = sampleFilteredImage;
        this.comparisonMap = comparisonMap;

    }

    public BufferedImage getComparisonMap() {
        return comparisonMap;
    }

    public void setComparisonMap(BufferedImage comparisonMap) {
        this.comparisonMap = comparisonMap;
    }

    public BufferedImage getOriginalFilteredImage() {
        return originalFilteredImage;
    }

    public void setOriginalFilteredImage(BufferedImage originalFilteredImage) {
        this.originalFilteredImage = originalFilteredImage;
    }

    public BufferedImage getSampleFilteredImage() {
        return sampleFilteredImage;
    }

    public void setSampleFilteredImage(BufferedImage sampleFilteredImage) {
        this.sampleFilteredImage = sampleFilteredImage;
    }
}
