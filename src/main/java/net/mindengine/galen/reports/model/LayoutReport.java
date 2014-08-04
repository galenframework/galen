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

import net.mindengine.galen.validation.ValidationError;

import java.util.LinkedList;
import java.util.List;

public class LayoutReport {
    
    private String title;
    
    private List<LayoutSection> sections = new LinkedList<LayoutSection>();

    private String screenshot;
    private String screenshotFullPath;
    private List<ValidationError> validationErrors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<LayoutSection> getSections() {
        return sections;
    }

    public void setSections(List<LayoutSection> sections) {
        this.sections = sections;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public String getScreenshotFullPath() {
        return screenshotFullPath;
    }

    public void setScreenshotFullPath(String screenshotFullPath) {
        this.screenshotFullPath = screenshotFullPath;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public int errors() {
        int errors = 0;
        if (validationErrors != null) {
            for (ValidationError validationError : validationErrors) {
                if (!validationError.isOnlyWarn()) {
                    errors++;
                }
            }
        }
        return errors;
    }

    public int warnings() {
        int warnings = 0;
        if (validationErrors != null) {
            for (ValidationError validationError : validationErrors) {
                if (validationError.isOnlyWarn()) {
                    warnings ++;
                }
            }
        }
        return warnings;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}
