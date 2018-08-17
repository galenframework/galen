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
package com.galenframework.generator.builders;

public class SpecGeneratorOptions {
    private int minimalStickyParentDistance = 60;
    private int minimalStickyHorizontalDistance = 60;
    private int minimalStickyVerticalDistance = 60;
    private boolean useGalenExtras = true;

    public SpecGeneratorOptions() {
    }

    public int getMinimalStickyParentDistance() {
        return minimalStickyParentDistance;
    }

    public void setMinimalStickyParentDistance(int minimalStickyParentDistance) {
        this.minimalStickyParentDistance = minimalStickyParentDistance;
    }

    public int getMinimalStickyHorizontalDistance() {
        return minimalStickyHorizontalDistance;
    }

    public void setMinimalStickyHorizontalDistance(int minimalStickyHorizontalDistance) {
        this.minimalStickyHorizontalDistance = minimalStickyHorizontalDistance;
    }

    public int getMinimalStickyVerticalDistance() {
        return minimalStickyVerticalDistance;
    }

    public void setMinimalStickyVerticalDistance(int minimalStickyVerticalDistance) {
        this.minimalStickyVerticalDistance = minimalStickyVerticalDistance;
    }

    public boolean isUseGalenExtras() {
        return useGalenExtras;
    }

    public SpecGeneratorOptions setUseGalenExtras(boolean useGalenExtras) {
        this.useGalenExtras = useGalenExtras;
        return this;
    }
}
