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

import java.util.LinkedList;
import java.util.List;

import com.galenframework.specs.Spec;

public class ObjectSpecs {

    private String objectName;
    private List<Spec> specs = new LinkedList<>();

    private List<SpecGroup> specGroups = new LinkedList<>();

    public ObjectSpecs(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public List<Spec> getSpecs() {
        return this.specs;
    }

    public void setSpecs(List<Spec> specs) {
        this.specs = specs;
    }

    public List<SpecGroup> getSpecGroups() {
        return specGroups;
    }

    public void setSpecGroups(List<SpecGroup> specGroups) {
        this.specGroups = specGroups;
    }

    public void addSpecGroup(SpecGroup specGroup) {
        if (specGroups == null) {
            specGroups = new LinkedList<>();
        }
        specGroups.add(specGroup);
    }

    public void addSpec(Spec spec) {
        if (specs == null) {
            specs = new LinkedList<>();
        }
        specs.add(spec);
    }
}
