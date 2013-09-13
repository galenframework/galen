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
package net.mindengine.galen.specs.reader.page;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.page.PageSection;


public class PageSpec {

    private static final List<String> EMPTY_TAGS = new LinkedList<String>();
    private Map<String, Locator> objects = new HashMap<String, Locator>();
    private Map<String, Locator> multiObjects = new HashMap<String, Locator>();
    private List<PageSection> sections = new LinkedList<PageSection>();

    public Map<String, Locator> getObjects() {
        return this.objects;
    }

    public void setObjects(Map<String, Locator> objects) {
        this.objects = objects;
    }

    public List<PageSection> getSections() {
        return this.sections;
    }

    public void setSections(List<PageSection> sections) {
        this.sections = sections;
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

    public List<PageSection> findSections(List<String> includedTags) {
        return findSections(includedTags, EMPTY_TAGS);
    }

    public List<PageSection> findSections(List<String> includedTags, List<String> excludedTags) {
        List<PageSection> filteredSections = new LinkedList<PageSection>();
        
        for (PageSection section : sections) {
            
            if (section.hasAnyTag(includedTags) && !section.hasAnyTag(excludedTags)) {
                filteredSections.add(section);
            }
        }
        return filteredSections;
    }

    /**
     * Find all objects that match simple regex
     * @param objectNameSimpleRegex - Regex which allows only '*' symbol in expresion
     * @return
     */
    public List<String> findMatchingObjectNames(String objectNameSimpleRegex) {
        
        Pattern pattern = Pattern.compile(objectNameSimpleRegex.replace("*", ".*"));
        List<String> foundObjects = new LinkedList<String>();
        
        for (String objectName : objects.keySet()) {
            if (pattern.matcher(objectName).matches()) {
                foundObjects.add(objectName);
            }
        }
        
        return foundObjects;
    }

    public Map<String, Locator> getMultiObjects() {
        return multiObjects;
    }

    public void setMultiObjects(Map<String, Locator> multiObjects) {
        this.multiObjects = multiObjects;
    }

    public void addMultiObject(String objectName, Locator locator) {
        multiObjects.put(objectName, locator);
    }

    public void updateMultiObjects(Page page) {
        for (Map.Entry<String, Locator> object : multiObjects.entrySet()) {
            Locator locator = object.getValue();
            
            int count = page.getObjectCount(locator);
            
            for (int index = 1; index <= count; index++) {
                String objectName = object.getKey().replace("*", Integer.toString(index));
                Locator newLocator = new Locator(locator.getLocatorType(), locator.getLocatorValue(), index);
                objects.put(objectName, newLocator);
            }
        }
    }

}
