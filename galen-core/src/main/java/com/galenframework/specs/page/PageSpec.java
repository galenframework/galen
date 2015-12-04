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
package com.galenframework.specs.page;

import java.util.*;
import java.util.regex.Pattern;

import com.galenframework.parser.AlphanumericComparator;
import com.galenframework.speclang2.specs.SpecReader;
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


    /**
     * Returns a list of all objects on page spec
     * @return
     */
    public Map<String, Locator> getObjects() {
        return this.objects;
    }

    /**
     * Clears current objects list and sets new object list
     * @param objects
     */
    public void setObjects(Map<String, Locator> objects) {
        this.objects.clear();
        if (objects != null) {
            this.objects.putAll(objects);
        }
    }

    /**
     * Clears the current object groups list and sets new group list
     * @param objectGroups
     */
    public void setObjectGroups(Map<String, List<String>> objectGroups) {
        this.objectGroups.clear();
        if (objectGroups != null) {
            this.objectGroups.putAll(objectGroups);
        }
    }

    /**
     * Returns list of root sections
     * @return
     */
    public List<PageSection> getSections() {
        return this.sections;
    }

    /**
     * Clears the current root sections and copies new sections from given list
     * @param sections
     */
    public void setSections(List<PageSection> sections) {
        this.sections.clear();
        if (sections != null) {
            this.sections.addAll(sections);
        }
    }

    /**
     * Adds a page section to root of the page spec
     * @param section
     */
    public void addSection(PageSection section) {
        sections.add(section);
    }

    /**
     * Adds object with given name and locator to page spec
     * @param objectName Name of object
     * @param locator Locator which is used for fetching object on page
     */
    public void addObject(String objectName, Locator locator) {
        objects.put(objectName, locator);
    }

    /**
     * Returns locator for a specific object
     * @param objectName Name of object
     */
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

    /**
     * Finds and returns sorted list of all objects matching the given object expression.
     * If the object in the expression is not found, it will still will be returned in a list
     *
     * @param objectExpression Galen object search expression
     *                         e.g. "menu.item-#, footer*, header, header.logo, &skeleton_group"
     */
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

    /**
     * Returns an alphanumericly sorted list of names of all declared objects
     */
    public List<String> getSortedObjectNames() {
        List<String> list = new ArrayList<String>(getObjects().keySet());
        Collections.sort(list, new AlphanumericComparator());
        return list;
    }

    /**
     * Find all objects belonging to a specific group
     * @param groupName A name of a an object group
     */
    public List<String> findObjectsInGroup(String groupName) {
        if (getObjectGroups().containsKey(groupName)) {
            return getObjectGroups().get(groupName);
        } else {
            return Collections.emptyList();
        }
    }


    /**
     * Merges all objects, sections and objectGroups from spec
     */
    public void merge(PageSpec spec) {
        if (spec == null) {
            throw new IllegalArgumentException("Cannot merge null spec");
        }
        objects.putAll(spec.getObjects());
        sections.addAll(spec.getSections());
        objectGroups.putAll(spec.getObjectGroups());
	}

    /**
     * Clears all existing sections
     */
    public void clearSections() {
        sections.clear();
    }

    /**
     * Parses the spec from specText and adds it to the page spec inside specified section. If section does not exit, it will create it
     * @param sectionName
     * @param objectName
     * @param specText
     */
    public void addSpec(String sectionName, String objectName, String specText) {
        PageSection pageSection = findSection(sectionName);

        if (pageSection == null) {
            pageSection = new PageSection(sectionName);
            sections.add(pageSection);
        }

        ObjectSpecs objectSpecs = new ObjectSpecs(objectName);
        objectSpecs.addSpec(new SpecReader().read(specText));
        pageSection.addObjects(objectSpecs);
    }

    private PageSection findSection(String sectionName) {
        for (PageSection section : sections) {
            if (section.getName().equals(sectionName)) {
                return section;
            }
        }
        return null;
    }

    public Map<String, List<String>> getObjectGroups() {
        return objectGroups;
    }

}
