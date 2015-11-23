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

import java.util.*;
import java.util.regex.Pattern;

import com.galenframework.speclang2.AlphanumericComparator;
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
     * Find all objects that match galen object statements
     * @param objectExpression - Galen object statements
     * @return
     */
    public List<String> findOnlyExistingMatchingObjectNames(String objectExpression) {
        String[] parts = objectExpression.split(",");

        List<String> allSortedObjectNames = getSortedObjectNames();
        List<String> resultingObjectNames = new LinkedList<String>();

        for (String part : parts) {
            String singleExpression = part.trim();
            if (!singleExpression.isEmpty()) {
                if (GalenUtils.isObjectGroup(singleExpression)) {
                    resultingObjectNames.addAll(findObjectsInGroup(GalenUtils.extractGroupName(singleExpression)));
                } else if (GalenUtils.isObjectsSearchExpression(singleExpression)) {
                    Pattern objectPattern = GalenUtils.convertObjectNameRegex(singleExpression);
                    for (String objectName : allSortedObjectNames) {
                        if (objectPattern.matcher(objectName).matches()) {
                            resultingObjectNames.add(objectName);
                        }
                    }
                } else if (objects.containsKey(singleExpression)) {
                    resultingObjectNames.add(singleExpression);
                }
            }
        }
        return resultingObjectNames;
    }

    public List<String> findAllObjectsMatchingStrictStatements(String objectExpression) {
        String[] parts = objectExpression.split(",");

        List<String> allSortedObjectNames = getSortedObjectNames();
        List<String> resultingObjectNames = new LinkedList<String>();

        for (String part : parts) {
            String singleExpression = part.trim();
            if (!singleExpression.isEmpty()) {
                if (GalenUtils.isObjectGroup(singleExpression)) {
                    resultingObjectNames.addAll(findObjectsInGroup(GalenUtils.extractGroupName(singleExpression)));
                } else if (GalenUtils.isObjectsSearchExpression(singleExpression)) {
                    Pattern objectPattern = GalenUtils.convertObjectNameRegex(singleExpression);
                    for (String objectName : allSortedObjectNames) {
                        if (objectPattern.matcher(objectName).matches()) {
                            resultingObjectNames.add(objectName);
                        }
                    }
                } else {
                    resultingObjectNames.add(singleExpression);
                }
            }
        }
        return resultingObjectNames;
    }

    public List<String> getSortedObjectNames() {
        List<String> list = new ArrayList<String>(getObjects().keySet());
        Collections.sort(list, new AlphanumericComparator());
        return list;
    }

    public List<String> findObjectsInGroup(String groupName) {
        if (getObjectGroups().containsKey(groupName)) {
            return getObjectGroups().get(groupName);
        } else {
            return Collections.emptyList();
        }
    }

    public void merge(PageSpec spec) {
		objects.putAll(spec.getObjects());
		sections.addAll(spec.getSections());
	}

    public Map<String, List<String>> getObjectGroups() {
        return objectGroups;
    }

}
