/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.page.Rect;

public class LayoutObject {
    
    private String name;
    private List<LayoutSpec> specs = new LinkedList<LayoutSpec>();
    private LayoutObject parent;
    
    // Here it will temporarily store sub objects that will be later picked up by spec
    private List<LayoutObject> subObjects = null;
    private Rect area;

    public LayoutObject() {
        
    }
    
    public LayoutObject(LayoutObject parent) {
        this.setParent(parent);
        if (parent != null) {
            parent.appendChildObject(this);
        }
    }

    private void appendChildObject(LayoutObject pageTestObject) {
        if (getSubObjects() == null) {
            setSubObjects(new LinkedList<LayoutObject>());
        }
        getSubObjects().add(pageTestObject);
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

    public LayoutObject getParent() {
        return parent;
    }

    public void setParent(LayoutObject parent) {
        this.parent = parent;
    }

    public List<LayoutObject> getSubObjects() {
        return subObjects;
    }

    public void setSubObjects(List<LayoutObject> subObjects) {
        this.subObjects = subObjects;
    }

    public void setArea(Rect area) {
        this.area = area;
    }
    public Rect getArea() {
        return this.area;
    }

}
