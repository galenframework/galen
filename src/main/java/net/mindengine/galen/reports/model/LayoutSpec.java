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
package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.ImageComparison;

public class LayoutSpec {
    
    private Place place;
    private String text;
    private Boolean failed = false;
    private List<String> errorMessages = new LinkedList<String>();
    private List<ErrorArea> errorAreas = new LinkedList<ErrorArea>();
    private List<LayoutObject> subObjects;
    private boolean onlyWarn;
    private ImageComparison imageComparison;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    public void setErrorAreas(List<ErrorArea> errorAreas) {
        this.errorAreas = errorAreas;
    }
    public List<ErrorArea> getErrorAreas() {
        return this.errorAreas;
    }

    public List<LayoutObject> getSubObjects() {
        return subObjects;
    }

    public void setSubObjects(List<LayoutObject> subObjects) {
        this.subObjects = subObjects;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setOnlyWarn(boolean onlyWarn) {
        this.onlyWarn = onlyWarn;
    }

    public boolean isOnlyWarn() {
        return onlyWarn;
    }

    public void setImageComparison(ImageComparison imageComparison) {
        this.imageComparison = imageComparison;
    }

    public ImageComparison getImageComparison() {
        return imageComparison;
    }
}
