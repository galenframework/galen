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
package com.galenframework.reports.model;

import java.util.LinkedList;
import java.util.List;

public class LayoutSection {

    private String name;
    
    private List<LayoutObject> objects = new LinkedList<>();
    private List<LayoutSection> sections;

    public LayoutSection(){
    }

    public LayoutSection(String name) {
        this.setName(name);
    }

    public List<LayoutObject> getObjects() {
        return objects;
    }

    public void setObjects(List<LayoutObject> objects) {
        this.objects = objects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LayoutObject findObject(String objectName) {
        if (objects != null) {
            for (LayoutObject object : objects) {
                if (object.getName().equals(objectName)) {
                    return object;
                }
            }
        }
        return null;
    }

    public List<LayoutSection> getSections() {
        return sections;
    }

    public void setSections(List<LayoutSection> sections) {
        this.sections = sections;
    }

    public void addSection(LayoutSection section) {
        if (sections == null) {
            sections = new LinkedList<>();
        }
        sections.add(section);
    }
}
