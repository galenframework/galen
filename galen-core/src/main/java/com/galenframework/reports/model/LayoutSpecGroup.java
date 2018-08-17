/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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

/**
 * Created by ishubin on 2015/02/28.
 */
public class LayoutSpecGroup {
    private String name;
    private List<LayoutSpec> specs;

    public List<LayoutSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<LayoutSpec> specs) {
        this.specs = specs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSpec(LayoutSpec spec) {
        if (specs == null) {
            specs = new LinkedList<>();
        }
        specs.add(spec);
    }
}
