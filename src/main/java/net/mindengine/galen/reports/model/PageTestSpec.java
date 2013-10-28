/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.validation.ErrorArea;

public class PageTestSpec {
    
    private String text;
    private Boolean failed = false;
    private String screenshot;
    private List<String> errorMessages = new LinkedList<String>();
    private List<ErrorArea> errorAreas = new LinkedList<ErrorArea>();
    private List<PageTestObject> subObjects;

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

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
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

    public List<PageTestObject> getSubObjects() {
        return subObjects;
    }

    public void setSubObjects(List<PageTestObject> subObjects) {
        this.subObjects = subObjects;
    }
}
