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

import java.util.LinkedList;
import java.util.List;

import com.galenframework.page.Rect;

public class LayoutObject {
    
    private String name;
    private List<LayoutSpec> specs = new LinkedList<>();
    private List<LayoutSpecGroup> specGroups;


    private Rect area;

    public LayoutObject() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LayoutSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<LayoutSpec> specs) {
        this.specs = specs;
    }

    public void setArea(Rect area) {
        this.area = area;
    }
    public Rect getArea() {
        return this.area;
    }

    public List<LayoutSpecGroup> getSpecGroups() {
        return specGroups;
    }

    public void setSpecGroups(List<LayoutSpecGroup> specGroups) {
        this.specGroups = specGroups;
    }

    public void addSpecGroup(LayoutSpecGroup specGroup) {
        if (this.specGroups == null) {
            this.specGroups = new LinkedList<>();
        }

        specGroups.add(specGroup);
    }
}
