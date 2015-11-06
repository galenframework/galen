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
package com.galenframework.specs.reader.page;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSection;
import com.galenframework.utils.GalenUtils;


public class PageSpec {

    private final Map<String, Locator> objects = new HashMap<>();
    private final List<PageSection> sections = new LinkedList<>();
    private final Map<String, List<String>> objectGroups = new HashMap<>();

    public PageSpec() {
    }

    public PageSpec(Map<String, Locator> objects) {
        setObjects(objects);
    }

    public Map<String, Locator> getObjects() {
        return this.objects;
    }

    public void setObjects(Map<String, Locator> objects) {
        this.objects.clear();
        if (objects != null) {
            this.objects.putAll(objects);
        }
    }
    public void setObjectGroups(Map<String, List<String>> objectGroups) {
        this.objectGroups.clear();
        if (objectGroups != null) {
            this.objectGroups.putAll(objectGroups);
        }
    }

    public List<PageSection> getSections() {
        return this.sections;
    }

    public void setSections(List<PageSection> sections) {
        this.sections.clear();
        if (sections != null) {
            this.sections.addAll(sections);
        }
    }

    public void addSection(PageSection section) {
        sections.add(section);
    }

    public void addObject(String objectName, Locator locator) {
        objects.put(objectName, locator);
    }

    public Locator getObjectLocator(String objectName) {
        return objects.get(objectName);
    }

    /**
     * Find all objects that match simple regex
     * @param objectNameSimpleRegex - Regex which allows only '*' symbol in expresion
     * @return
     */
    public List<String> findMatchingObjectNames(String objectNameSimpleRegex) {
        String[] textPatterns = objectNameSimpleRegex.split(",");
        List<Pattern> patterns = new LinkedList<>();

        for (String textPattern : textPatterns) {
            patterns.add(GalenUtils.convertObjectNameRegex(textPattern.trim()));
        }
        List<String> foundObjects = new LinkedList<>();
        
        for (String objectName : objects.keySet()) {
            if (oneOfPatternsMatches(patterns, objectName)) {
                foundObjects.add(objectName);
            }
        }
        
        return foundObjects;
    }

    private boolean oneOfPatternsMatches(List<Pattern> patterns, String objectName) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(objectName).matches()) {
                return true;
            }
        }
        return false;
    }

    public void merge(PageSpec spec) {
		objects.putAll(spec.getObjects());
		sections.addAll(spec.getSections());
	}

    public Map<String, List<String>> getObjectGroups() {
        return objectGroups;
    }

}
