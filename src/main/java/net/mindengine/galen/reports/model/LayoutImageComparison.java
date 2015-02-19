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
package net.mindengine.galen.reports.model;

/**
 * Created by ishubin on 2/17/15.
 */
public class LayoutImageComparison {

    private String actualImage;
    private String expectedImage;
    private String comparisonMapImage;

    public String getActualImage() {
        return actualImage;
    }

    public void setActualImage(String actualImage) {
        this.actualImage = actualImage;
    }

    public String getExpectedImage() {
        return expectedImage;
    }

    public void setExpectedImage(String expectedImage) {
        this.expectedImage = expectedImage;
    }

    public String getComparisonMapImage() {
        return comparisonMapImage;
    }

    public void setComparisonMapImage(String comparisonMapImage) {
        this.comparisonMapImage = comparisonMapImage;
    }
}
