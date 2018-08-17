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

import com.galenframework.reports.nodes.TestReportNode;
import com.galenframework.specs.Place;

public class LayoutSpec {

    private TestReportNode.Status status = TestReportNode.Status.INFO;
    
    private Place place;
    private String name;
    private List<String> errors;
    private List<LayoutMeta> meta;

    // List of object names to be highlighted in report
    private List<String> highlight = new LinkedList<>();
    private LayoutImageComparison imageComparison;

    // Here it will temporarily store sub objects that will be later picked up by spec
    private LayoutReport subLayout;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }


    public TestReportNode.Status getStatus() {
        return status;
    }

    public void setStatus(TestReportNode.Status status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getHighlight() {
        return highlight;
    }

    public void setHighlight(List<String> highlight) {
        this.highlight = highlight;
    }

    public LayoutReport getSubLayout() {
        return subLayout;
    }

    public void setSubLayout(LayoutReport subLayout) {
        this.subLayout = subLayout;
    }

    public LayoutImageComparison getImageComparison() {
        return imageComparison;
    }

    public void setImageComparison(LayoutImageComparison imageComparison) {
        this.imageComparison = imageComparison;
    }

    public List<LayoutMeta> getMeta() {
        return meta;
    }

    public void setMeta(List<LayoutMeta> meta) {
        this.meta = meta;
    }
}
