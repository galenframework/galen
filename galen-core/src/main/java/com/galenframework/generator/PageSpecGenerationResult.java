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
package com.galenframework.generator;

import java.util.List;

public class PageSpecGenerationResult {
    private Size size;
    private final List<String> objectNames;
    private final List<PageItemNode> objects;
    private final SuggestionTestResult suggestionResults;

    public PageSpecGenerationResult(Size size, List<String> objectNames, List<PageItemNode> objects, SuggestionTestResult suggestionResults) {
        this.size = size;
        this.objectNames = objectNames;
        this.objects = objects;
        this.suggestionResults = suggestionResults;
    }

    public List<PageItemNode> getObjects() {
        return objects;
    }

    public SuggestionTestResult getSuggestionResults() {
        return suggestionResults;
    }

    public List<String> getObjectNames() {
        return objectNames;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
