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
package net.mindengine.galen.reports.model;

import java.util.LinkedList;
import java.util.List;

public class PageTestObject {
    
    private String name;
    private List<PageTestSpec> specs = new LinkedList<PageTestSpec>();
    private PageTestObject parent;
    
    // Here it will temporarily store sub objects that will be later picked up by spec
    private List<PageTestObject> subObjects = null;

    public PageTestObject() {
        
    }
    
    public PageTestObject(PageTestObject parent) {
        this.setParent(parent);
        if (parent != null) {
            parent.appendChildObject(this);
        }
    }

    private void appendChildObject(PageTestObject pageTestObject) {
        if (getSubObjects() == null) {
            setSubObjects(new LinkedList<PageTestObject>());
        }
        getSubObjects().add(pageTestObject);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageTestSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PageTestSpec> specs) {
        this.specs = specs;
    }

    public PageTestObject getParent() {
        return parent;
    }

    public void setParent(PageTestObject parent) {
        this.parent = parent;
    }

    public List<PageTestObject> getSubObjects() {
        return subObjects;
    }

    public void setSubObjects(List<PageTestObject> subObjects) {
        this.subObjects = subObjects;
    }

}
