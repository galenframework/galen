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
package com.galenframework.generator.model;

import java.util.LinkedList;
import java.util.List;

public class GmObjectSpecs {
    private String objectName;
    private List<GmSpec> specs = new LinkedList<>();

    public GmObjectSpecs(String objectName) {
        this.objectName = objectName;
    }

    public GmObjectSpecs(String objectName, List<GmSpec> specs) {
        this.objectName = objectName;
        this.specs = specs;
    }

    public List<GmSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<GmSpec> specs) {
        this.specs = specs;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
