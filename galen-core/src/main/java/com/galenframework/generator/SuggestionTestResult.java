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
package com.galenframework.generator;

import com.galenframework.generator.filters.SpecFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SuggestionTestResult {

    // key - is the name of the parent, value - a list of generated rules
    private Map<String, List<SpecStatement>> generatedRules;
    private Map<String, List<SpecStatement>> generatedObjectSpecs;
    private List<SpecFilter> filters;

    public Map<String, List<SpecStatement>> getGeneratedObjectSpecs() {
        return generatedObjectSpecs;
    }

    public SuggestionTestResult setGeneratedObjectSpecs(Map<String, List<SpecStatement>> generatedObjectSpecs) {
        this.generatedObjectSpecs = generatedObjectSpecs;
        return this;
    }

    public boolean isValid() {
        return (generatedRules != null && generatedRules.size() > 0)
            || (generatedObjectSpecs != null && generatedObjectSpecs.size() > 0);
    }

    public List<SpecFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SpecFilter> filters) {
        this.filters = filters;
    }

    public SuggestionTestResult addObjectSpec(String objectName, SpecStatement spec) {
        if (this.generatedObjectSpecs == null) {
            this.generatedObjectSpecs = new HashMap<>();
        }
        List<SpecStatement> existingSpecs = makeSureListExists(this.generatedObjectSpecs, objectName);
        existingSpecs.add(spec);
        return this;
    }

    public SuggestionTestResult addObjectSpecs(String objectName, List<SpecStatement> specs) {
        if (specs != null && specs.size() > 0) {
            if (this.generatedObjectSpecs == null) {
                this.generatedObjectSpecs = new HashMap<>();
            }

            List<SpecStatement> existingSpecs = makeSureListExists(this.generatedObjectSpecs, objectName);
            existingSpecs.addAll(specs);
        }

        return this;
    }

    public SuggestionTestResult addFilter(SpecFilter filter) {
        if (filters == null) {
            filters = new LinkedList<>();
        }
        filters.add(filter);

        return this;
    }

    public void merge(SuggestionTestResult result) {
        if (result != null) {
            if (this.generatedObjectSpecs == null) {
                this.generatedObjectSpecs = new HashMap<>();
            }
            mergeMapList(this.generatedObjectSpecs, result.getGeneratedObjectSpecs());

            if (this.generatedRules == null) {
                this.generatedRules = new HashMap<>();
            }
            mergeMapList(this.generatedRules, result.generatedRules);
        }
    }

    private void mergeMapList(Map<String, List<SpecStatement>> origin, Map<String, List<SpecStatement>> other) {
        if (other != null) {
            other.forEach((name, otherList) -> {
                List<SpecStatement> originList = makeSureListExists(origin, name);
                originList.addAll(otherList);
            });
        }
    }

    private List<SpecStatement> makeSureListExists(Map<String, List<SpecStatement>> origin, String name) {
        List<SpecStatement> originList = origin.get(name);
        if (originList == null) {
            originList = new LinkedList<>();
            origin.put(name, originList);
        } return originList;
    }

    public Map<String, List<SpecStatement>> getGeneratedRules() {
        return generatedRules;
    }

    public SuggestionTestResult setGeneratedRules(Map<String, List<SpecStatement>> generatedRules) {
        this.generatedRules = generatedRules;
        return this;
    }

    public SuggestionTestResult addGeneratedRule(String parentName, SpecStatement rule) {
        if (this.generatedRules == null) {
            this.generatedRules = new HashMap<>();
        }
        List<SpecStatement> list = makeSureListExists(this.generatedRules, parentName);

        list.add(rule);
        return this;
    }
}
