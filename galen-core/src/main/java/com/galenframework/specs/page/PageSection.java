/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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


import com.galenframework.specs.Place;

import java.util.LinkedList;
import java.util.List;

public class PageSection {

    private List<ObjectSpecs> objects = new LinkedList<>();
    private String name;
    private Place place;

    private List<PageSection> sections = new LinkedList<>();

    public PageSection() {
    }

    public PageSection(String name) {
        setName(name);
    }

    public PageSection(String name, Place place) {
        this.name = name;
        this.place = place;
    }


    public List<ObjectSpecs> getObjects() {
        return this.objects;
    }

    public void setObjects(List<ObjectSpecs> objects) {
        this.objects = objects;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<PageSection> getSections() {
        return sections;
    }

    public void setSections(List<PageSection> sections) {
        this.sections = sections;
    }

    public void addSubSection(PageSection subSection) {
        getSections().add(subSection);
    }

    public void mergeSection(PageSection section) {
        if (section.getObjects() != null) {
            if (this.objects == null) {
                this.objects = new LinkedList<>();
            }

            for (ObjectSpecs object : section.getObjects()) {
                this.objects.add(object);
            }
        }

        if (section.getSections() != null) {
            if (this.sections == null) {
                this.sections = new LinkedList<>();
            }

            for (PageSection subSection : section.getSections()) {
                this.sections.add(subSection);
            }
        }
    }

    public void addObjects(ObjectSpecs objectSpecs) {
        if (objects == null) {
            objects = new LinkedList<>();
        }

        objects.add(objectSpecs);
    }

    public boolean isEmpty() {
        return countAllObjectsRecursively() == 0;
    }

    private int countAllObjectsRecursively() {
        int amount = 0;
        if (objects != null) {
            amount = objects.size();
        }

        if (sections != null) {
            for (PageSection subSection : sections) {
                amount += subSection.countAllObjectsRecursively();
            }
        }

        return amount;
    }

    public PageSection cleanSection() {
        PageSection cleanedSection = new PageSection(name, place);
        cleanedSection.setObjects(objects);

        if (sections != null) {
            for (PageSection subSection : sections) {
                PageSection cleanedSubSection = subSection.cleanSection();
                if (!cleanedSubSection.isEmpty()) {
                    cleanedSection.getSections().add(cleanedSubSection);
                }
            }
        }

        return cleanedSection;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
