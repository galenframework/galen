/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.specs.page;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Spec;

public class ObjectSpecs {

    private String objectName;
    private List<Spec> specs = new LinkedList<Spec>();

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

}
