/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.galenframework.validation.ValidationResult;
import com.galenframework.rainbow4j.Rainbow4J;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LayoutReport {
    
    private String title;
    private List<String> includedTags = new ArrayList<>();
    private List<String> excludedTags = new ArrayList<>();
    
    private List<LayoutSection> sections = new LinkedList<>();
    private Map<String, LayoutObjectDetails> objects = new HashMap<>();

    private String screenshot;

    @JsonIgnore
    private List<ValidationResult> validationErrorResults;

    /**
     Used to store temporary files which could be saved later.
     Everything that refers to a report file like screenshot or image comparison files
     will actually point to a name in @fileStorage container.
     Once the HTML or JSON report is about to be rendered - it will copy those files to the report folder.
     */
    @JsonIgnore
    private FileTempStorage fileStorage = new FileTempStorage("layout");


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

    public void setValidationErrorResults(List<ValidationResult> validationErrorResults) {
        this.validationErrorResults = validationErrorResults;
    }

    public int errors() {
        int errors = 0;
        if (validationErrorResults != null) {
            for (ValidationResult validationError : validationErrorResults) {
                if (!validationError.getError().isOnlyWarn()) {
                    errors++;
                }
            }
        }
        return errors;
    }

    public int warnings() {
        int warnings = 0;
        if (validationErrorResults != null) {
            for (ValidationResult validationError : validationErrorResults) {
                if (validationError.getError().isOnlyWarn()) {
                    warnings ++;
                }
            }
        }
        return warnings;
    }

    public List<ValidationResult> getValidationErrorResults() {
        return validationErrorResults;
    }

    public Map<String, LayoutObjectDetails> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, LayoutObjectDetails> objects) {
        this.objects = objects;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public FileTempStorage getFileStorage() {
        return fileStorage;
    }

    public String registerFile(String fileName, File file) {
        return fileStorage.registerFile(fileName, file);

    }
}
