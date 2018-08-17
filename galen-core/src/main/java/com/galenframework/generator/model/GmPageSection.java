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
package com.galenframework.generator.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GmPageSection {
    private String name;
    private List<GmSpecRule> rules = new LinkedList<>();
    private List<GmObjectSpecs> objectSpecs = new LinkedList<>();


    public GmPageSection optimizeSection() {
        GmPageSection section = new GmPageSection(name);
        section.setRules(rules);

        Map<String, List<String>> specsMap = groupBySpecs();
        Map<String, List<GmSpec>> regroupedSpecs = new HashMap<>();

        specsMap.forEach((spec, objects) -> {
            String objectNames = StringUtils.join(objects, ", ");
            List<GmSpec> specs = regroupedSpecs.get(objectNames);
            if (specs == null) {
                specs = new LinkedList<>();
                regroupedSpecs.put(objectNames, specs);
            }
            specs.add(new GmSpec(spec));
        });

        regroupedSpecs.forEach((name, specs) -> section.getObjectSpecs().add(new GmObjectSpecs(name, specs)));

        section.getObjectSpecs().forEach(os -> Collections.sort(os.getSpecs(), bySpecText()));
        return section;
    }

    private Comparator<GmSpec> bySpecText() {
        return (a, b) -> a.getStatement().compareTo(b.getStatement());
    }

    private Map<String, List<String>> groupBySpecs() {
        Map<String, List<String>> specsMap = new HashMap<>();
        objectSpecs.forEach(object ->
            object.getSpecs().forEach(spec -> {
                List<String> objectsPerSpec = specsMap.get(spec.getStatement());
                if (objectsPerSpec == null) {
                    objectsPerSpec = new LinkedList<>();
                    specsMap.put(spec.getStatement(), objectsPerSpec);
                }

                if (!objectsPerSpec.contains(object.getObjectName())) {
                    objectsPerSpec.add(object.getObjectName());
                }
            })
        );
        return specsMap;
    }

    public GmPageSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GmSpecRule> getRules() {
        return rules;
    }

    public void setRules(List<GmSpecRule> rules) {
        this.rules = rules;
    }

    public List<GmObjectSpecs> getObjectSpecs() {
        return objectSpecs;
    }

    public void setObjectSpecs(List<GmObjectSpecs> objectSpecs) {
        this.objectSpecs = objectSpecs;
    }

    public boolean getHasContent() {
        return getHasRules()
            || (objectSpecs != null && !objectSpecs.isEmpty());
    }

    public boolean getHasRules() {
        return (rules != null && !rules.isEmpty());
    }
}
